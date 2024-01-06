package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.exceptionHandling.RentAlreadyExistsForReservationException;
import pl.sda.carrental.model.DTO.RentDTO;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.repository.EmployeeRepository;
import pl.sda.carrental.repository.RentRepository;
import pl.sda.carrental.repository.ReservationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentService {
    private final RentRepository rentRepository;
    private final ReservationRepository reservationRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Gets all Rent objects
     *
     * @return List of all Rent objects
     */
    public List<Rent> allRents() {
        return rentRepository.findAll();
    }

    /**
     * The save method is responsible for creating a new rent based on the provided RentDTO and saving it to the repository
     *
     * @param rentDTO An object containing rent data
     * @return The newly created and saved rent object
     */
    public Rent save(RentDTO rentDTO) {
        Rent rent = new Rent();
        updateRentDetails(rentDTO, rent);

        return rentRepository.save(rent);
    }

    /**
     * The editRent method is a transactional operation that allows for the modification of an existing rent based on the provided
     * rent ID and updated rent details in the RentDTO. It retrieves the rent by ID from the repository, updates its details using
     * the updateRentDetails method, deletes the existing rent, and then saves the modified rent back to the repository
     *
     * @param id The identifier of the rent to be edited
     * @param rentDTO An object containing updated rent data
     * @return The modified rent object
     * @throws ObjectNotFoundInRepositoryException if no rent is found with the provided ID
     */
    public Rent editRent(Long id, RentDTO rentDTO) {
        Rent rent = rentRepository.findById(id)
                        .orElseThrow(() ->
                                new ObjectNotFoundInRepositoryException("No rent under ID #" + id));
        updateRentDetails(rentDTO, rent);

        rentRepository.deleteById(id);
        return rentRepository.save(rent);
    }

    /**
     * Deletes Rent object using ID
     *
     * @param id The identifier of the rent to be deleted
     * @throws ObjectNotFoundInRepositoryException if no rent is found with the provided ID
     */
    public void deleteRentById(Long id) {
        rentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No rent under ID #" + id));
        rentRepository.deleteById(id);
    }

    /**
     * The updateRentDetails method is responsible for updating the details of a Rent object based on the information provided in the RentDTO.
     * It checks if a rent already exists for the specified reservation ID, updates the associated employee, comments, rent date, and
     * reservation for the given Rent object
     *
     * @param rentDTO An object containing updated rent data
     * @param rent The Rent object to be updated
     * @throws RentAlreadyExistsForReservationException if a rent already exists for the specified reservation ID
     * @throws ObjectNotFoundInRepositoryException if no employee or reservation is found with the provided ID
     */
    private void updateRentDetails(RentDTO rentDTO, Rent rent) {
        List<Long> reservationsIds = rentRepository.findRentalsWithReservationId(rentDTO.reservationId());
        if(!reservationsIds.isEmpty()) {
            throw new RentAlreadyExistsForReservationException("Rent already exists for reservation with id "
                    + rentDTO.reservationId());
        }

        Employee foundEmployee = employeeRepository.findById(rentDTO.employeeId())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under ID #"
                        + rentDTO.employeeId()));

        rent.setEmployee(foundEmployee);
        rent.setComments(rentDTO.comments());
        rent.setRentDate(rentDTO.rentDate());

        // ======== tutaj nie muszę ustawiać rezerwację? ==========
        Reservation reservationFromRepository = reservationRepository.findById(rentDTO.reservationId())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Reservation with id "
                        + rentDTO.reservationId() + " not found"));

        rent.setReservation(reservationFromRepository);
        // ======================================================
    }
}
