package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.exceptionHandling.ReservationTimeCollisionException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.Client;
import pl.sda.carrental.model.DTO.ReservationDTO;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RentRepository rentRepository;
    private final ReturnRepository returnRepository;

    /**
     * Gets all Reservation Objects
     *
     * @return List of all Reservations
     */
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::mapReservationToDTO)
                .toList();
    }
    private ReservationDTO mapReservationToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getClient().getClient_id(),
                reservation.getCar().getCar_id(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStartBranch().getBranch_id(),
                reservation.getEndBranch().getBranch_id(),
                reservation.getRent(),
                reservation.getReturnal()
        );
    }

    /**
     * The saveReservation method creates a new reservation, updates its details based on the provided ReservationDTO, and saves it to the
     * repository within a transaction
     *
     * @param reservationDto An object containing reservation data
     * @return The newly created and saved reservation object
     */
    @Transactional
    public Reservation saveReservation(ReservationDTO reservationDto) {
        Reservation reservation = new Reservation();
        updateReservationDetails(reservationDto, reservation);
        return reservationRepository.save(reservation);
    }


    /**
     * The editReservation method is a transactional operation that allows for the modification of an existing reservation based on
     * the provided reservation ID and updated reservation details in the ReservationDTO.
     * It retrieves the reservation by ID from the repository, updates its details using the updateReservationDetails method,
     * and then saves the modified reservation back to the repository
     *
     * @param id The identifier of the reservation to be edited
     * @param reservationDTO An object containing updated reservation data
     * @return The modified reservation object
     * @throws ObjectNotFoundInRepositoryException if no reservation is found with the provided ID.
     */
    @Transactional
    public Reservation editReservation(Long id, ReservationDTO reservationDTO) {
        Reservation foundReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No reservation under ID #" + id));
        updateReservationDetails(reservationDTO, foundReservation);
        return reservationRepository.save(foundReservation);
    }

    /**
     * The updateReservationDetails method is responsible for updating the details of a given reservation based on the information
     * provided in the ReservationDTO. It sets the start and end branches, start and end dates, checks for car availability,
     * associates the car and client with the reservation, calculates the price based on the reservation duration, and handles
     * potential conflicts with existing reservations
     *
     * @param reservationDto Object containing updated reservation dat
     * @param reservation The reservation object to be updated
     * @throws ObjectNotFoundInRepositoryException if no car or customer is found with the provided ID.
     * @throws ReservationTimeCollisionException if there are time collisions with existing reservations for the selected car
     */
    private void updateReservationDetails(ReservationDTO reservationDto, Reservation reservation) {
        setStartEndBranch(reservationDto, reservation);
        reservation.setStartDate(reservationDto.startDate());
        reservation.setEndDate(reservationDto.endDate());

        Car carFromRepo = carRepository.findById(reservationDto.car_id())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under that ID"));

        if (!carFromRepo.getReservations().isEmpty()) {
            List<DateTimePeriod> timeCollision = carFromRepo.getReservations().stream()
                    .map(resObject -> new DateTimePeriod(resObject.getStartDate(), resObject.getEndDate()))
                    .filter(dtp -> isDateSuitable(reservationDto, dtp))
                    .toList();
            if (!timeCollision.isEmpty()) {
                throw new ReservationTimeCollisionException("Car cannot be reserved for given time period!");
            }
        }
        reservation.setCar(carFromRepo);

        Client clientFromRepo = clientRepository.findById(reservationDto.customer_id())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No customer under that ID"));
        reservation.setClient(clientFromRepo);

        long daysDifference = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        BigDecimal price = carFromRepo.getPrice().multiply(BigDecimal.valueOf(daysDifference));
        reservation.setPrice(price);
    }

    /**
     * The isDateSuitable method is used to check if a given time period (DateTimePeriod) is suitable for a reservation
     * with the provided data (ReservationDTO)
     *
     * @param reservationDto An object containing reservation data, such as the start date and end date
     * @param dtp An object representing the time period to be checked
     * @return true if the period is suitable, and false otherwise
     */
    private boolean isDateSuitable(ReservationDTO reservationDto, DateTimePeriod dtp) {
        return dtp.start().equals(reservationDto.startDate()) ||
                dtp.end().equals(reservationDto.endDate()) ||

                (dtp.start().isAfter(reservationDto.startDate()) &&
                        dtp.start().isBefore(reservationDto.endDate())) ||

                (dtp.end().isAfter(reservationDto.startDate()) &&
                        dtp.end().isBefore(reservationDto.endDate())) ||

                (dtp.start().isAfter(reservationDto.startDate()) &&
                        dtp.end().isBefore(reservationDto.endDate())) ||

                (dtp.start().isBefore(reservationDto.startDate()) &&
                        dtp.end().isAfter(reservationDto.endDate()));
    }

    /**
     * Sets new start and end branches
     *
     * @param reservationDto Object containing start and end branch data
     * @param reservation Object for which the start and end branches are to be set
     * @throws ObjectNotFoundInRepositoryException if no employee or reservation is found with the provided ID
     */
    private void setStartEndBranch(ReservationDTO reservationDto, Reservation reservation) {
        Branch startBranch = branchRepository.findById(reservationDto.startBranchId())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Branch not found"));
        reservation.setStartBranch(startBranch);
        Branch endBranch = branchRepository.findById(reservationDto.endBranchId())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Branch not found"));
        reservation.setEndBranch(endBranch);
    }

    /**
     * Deletes reservation object using ID
     *
     * @param id The identifier of the Reservation to be deleted
     */
    @Transactional
    public void deleteReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No reservation under ID #" + id));
        reservationRepository.delete(reservation);
    }
}

record DateTimePeriod(LocalDate start, LocalDate end) {
}