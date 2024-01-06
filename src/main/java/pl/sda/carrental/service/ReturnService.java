package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.exceptionHandling.ReturnAlreadyExistsForReservationException;
import pl.sda.carrental.model.DTO.ReturnDTO;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.model.Returnal;
import pl.sda.carrental.repository.EmployeeRepository;
import pl.sda.carrental.repository.ReservationRepository;
import pl.sda.carrental.repository.ReturnRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ReturnService {
    private final ReservationRepository reservationRepository;
    private final ReturnRepository returnRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Gets all Return Objects
     *
     * @return List of all Return objects
     */
    public List<Returnal> getAllReturnals() {
        return returnRepository.findAll();
    }

    /**
     * The saveReturn method is responsible for creating a new return record based on the provided ReturnDTO and saving it to the repository.
     * It checks if a return already exists for the specified reservation ID and throws an exception if found
     *
     * @param returnDTO An object containing return data
     * @return The newly created and saved return object
     * @throws ReturnAlreadyExistsForReservationException if returnal with the provided ID already exists
     */
    @Transactional
    public Returnal saveReturn(ReturnDTO returnDTO) {
        List<Long> reservationsIds = returnRepository.findReturnsWithReservationId(returnDTO.reservationId());
        if(!reservationsIds.isEmpty()) {
            throw new ReturnAlreadyExistsForReservationException("Return already exists for reservation with id " + returnDTO.reservationId());
        }

        Returnal returnal = new Returnal();
        updateReturnalDetails(returnDTO, returnal);

        return returnRepository.save(returnal);
    }

    /**
     * The editReturnal method is a transactional operation that allows for the modification of an existing returnal
     * based on the provided returnal ID and updated returnal details in the ReturnDTO. It retrieves the returnal by ID
     * from the repository, updates its details using the updateReturnalDetails method, and then saves the modified
     * returnal back to the repository
     *
     * @param id The identifier of the returnal to be edited
     * @param returnDTO An object containing updated returnal data
     * @return The modified returnal object
     * @throws ObjectNotFoundInRepositoryException if no returnal is found with the provided ID
     */
    @Transactional
    public Returnal editReturnal(Long id, ReturnDTO returnDTO) {
        Returnal returnal = returnRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No returnal under ID #" + id));
        updateReturnalDetails(returnDTO, returnal);

        return returnRepository.save(returnal);
    }

    /**
     *he updateReturnalDetails method is responsible for updating the details of a Returnal object based on the information
     * provided in the ReturnDTO. It sets the return date, comments, upcharge, associated employee, and reservation for the
     * given Returnal object
     *
     * @param returnDTO An object containing updated returnal data
     * @param returnalToSave object to be updated
     * @throws ObjectNotFoundInRepositoryException if no employee or reservation is found with the provided ID
     */
    private void updateReturnalDetails(ReturnDTO returnDTO, Returnal returnalToSave) {

        returnalToSave.setReturnDate(returnDTO.returnDate());
        returnalToSave.setComments(returnDTO.comments());
        returnalToSave.setUpcharge(returnDTO.upcharge());

        Employee employeeFromRepository = employeeRepository.findById(returnDTO.employee())
                        .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under ID #" + returnDTO.employee()));

        returnalToSave.setEmployee(employeeFromRepository);

        Reservation reservationFromRepository = reservationRepository.findById(returnDTO.reservationId())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Reservation with id "
                        + returnDTO.reservationId() + " not found"));

        returnalToSave.setReservation(reservationFromRepository);
    }

    /**
     * deletes selected by ID object
     *
     * @param id The identifier of the returnal to be deleted
     * @throws ObjectNotFoundInRepositoryException if no returnal is found with the provided ID
     */
    @Transactional
    public void deleteReturnal(Long id) {
        returnRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No returnal under ID #" + id));
        returnRepository.deleteById(id);
    }
}
