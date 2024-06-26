package pl.sda.carrental.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sda.carrental.configuration.auth.model.Client;
import pl.sda.carrental.configuration.auth.repository.ClientRepository;
import pl.sda.carrental.configuration.auth.repository.UserRepository;
import pl.sda.carrental.configuration.auth.util.LoginUtils;
import pl.sda.carrental.exceptionHandling.ObjectAlreadyAssignedToBranchException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.model.Returnal;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.RentRepository;
import pl.sda.carrental.repository.ReservationRepository;
import pl.sda.carrental.repository.ReturnRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final RentRepository rentRepository;
    private final ReturnRepository returnRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Retrieves a client by their unique ID.
     *
     * @param id The unique identifier of the client to retrieve.
     * @return The Client object corresponding to the provided ID.
     * @throws ObjectNotFoundInRepositoryException if no client is found with the specified ID.
     */
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Client not found"));
    }

    /**
     * Retrieves a list containing all clients available in the repository.
     *
     * @return A list containing all Client objects available in the repository.
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Edits the details of a client identified by the provided ID.
     * Retrieves the client with the given ID from the repository or throws an exception if not found.
     * Then found Client is retrieved from parent branch and modified according to given parameter.
     * Parent and child object are saved to appropriate repositories in order to maintain all changes.
     *
     * @param id     The ID of the client to be updated.
     * @param client The Client object containing the updated information for the client.
     * @return The updated Client object.
     * @throws ObjectNotFoundInRepositoryException if no client is found under the provided ID.
     */
    @Transactional
    public Client editClient(Long id, Client client) {
        Client childClient = clientRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No client under that ID!"));
        Branch parentBranch = childClient.getBranch();

        parentBranch.getClients().stream()
                    .filter(filteredClient -> filteredClient.equals(childClient))
                    .findFirst().orElseThrow(() ->
                            new ObjectNotFoundInRepositoryException("No client under ID #" +
                                    id + " in that branch"));

        LoginUtils.checkDuplicateLogin(client.getLogin(), userRepository);

        childClient.setLogin(client.getLogin());
        childClient.setPassword(passwordEncoder.encode(client.getPassword()));
        childClient.setName(client.getName());
        childClient.setSurname(client.getSurname());
        childClient.setEmail(client.getEmail());
        childClient.setAddress(client.getAddress());

        branchRepository.save(parentBranch);
        return clientRepository.save(childClient);
    }

    /**
     * Checks if a client with the given ID exists in the repository, or throws an exception if not found.
     * Removes a client identified by the provided ID from the repository.
     * When Client is removed, all their reservations with corresponding rents and returns are removed as well.
     * In order to maintain logic integrity and prevent SQL foreign key violations, firstly, removes rents and returns,
     * secondly - reservations and lastly - client.
     *
     * @param id The ID of the client to be removed.
     * @throws ObjectNotFoundInRepositoryException if no client is found under the provided ID.
     */
    @Transactional
    public void removeClient(Long id) {
        clientRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No client under that ID!"));

        List<Rent> rentsAssociatedWithClient = rentRepository.findAll().stream()
                .filter(rent -> rent.getReservation().getClient().getId().equals(id))
                .toList();
        List<Returnal> returnsAssociatedWithClient = returnRepository.findAll().stream()
                .filter(returnal -> returnal.getReservation().getClient().getId().equals(id))
                .toList();
        List<Reservation> reservationsAssociatedWithClient = reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getClient().getId().equals(id))
                .toList();

        rentRepository.deleteAll(rentsAssociatedWithClient);
        returnRepository.deleteAll(returnsAssociatedWithClient);
        reservationRepository.deleteAll(reservationsAssociatedWithClient);

        clientRepository.deleteById(id);
    }

    /**
     * Assigns a client to a branch based on their respective IDs.
     * Retrieves the client with the given ID from the repository or throws an exception if not found.
     * Checks if the client is already assigned to a branch, throws an exception if already assigned.
     * Retrieves the branch with the given ID from the repository or throws an exception if not found.
     * Assigns the client to the branch and updates their association in the repositories.
     *
     * @param clientId The ID of the client to be assigned to a branch.
     * @param branchId The ID of the branch to which the client will be assigned.
     * @throws ObjectNotFoundInRepositoryException    if no client or branch is found under the provided IDs.
     * @throws ObjectAlreadyAssignedToBranchException if the client is already assigned to an existing branch.
     */
    @Transactional
    public Client assignClientToBranch(Long clientId, Long branchId) {
        Client foundClient = clientRepository.findById(clientId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No client under ID #" + clientId));
        if (foundClient.getBranch() != null) {
            throw new ObjectAlreadyAssignedToBranchException("This client is already assigned to existing branch!");
        }
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));

        foundBranch.getClients().add(foundClient);
        foundClient.setBranch(foundBranch);

        branchRepository.save(foundBranch);
        return clientRepository.save(foundClient);
    }

    /**
     * Removes a specific client from a branch based on their respective IDs.
     * The client is dissociated from the branch, and the branch-client association is updated in the repositories.
     *
     * @param clientId The ID of the client to be removed from the branch.
     * @param branchId The ID of the branch from which the client will be removed.
     * @throws ObjectNotFoundInRepositoryException if no branch or client is found under the provided IDs.
     */
    @Transactional
    public void removeClientFromBranch(Long clientId, Long branchId) {
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));
        Client foundClient = foundBranch.getClients().stream()
                .filter(client -> Objects.equals(client.getId(), clientId))
                .findFirst()
                .orElseThrow(() ->
                        new ObjectNotFoundInRepositoryException("No client under ID #"
                                + clientId + " is assigned to branch under ID #" + branchId));

        foundClient.setBranch(null);
        branchRepository.save(foundBranch);
    }
}
