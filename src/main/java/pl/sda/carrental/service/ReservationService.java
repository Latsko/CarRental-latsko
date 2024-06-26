package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.exceptionHandling.ReservationTimeCollisionException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.configuration.auth.model.Client;
import pl.sda.carrental.model.DTO.ReservationDTO;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRepository;
import pl.sda.carrental.configuration.auth.repository.ClientRepository;
import pl.sda.carrental.repository.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final ClientRepository clientRepository;
    private final RevenueService revenueService;
    private final BigDecimal CROSS_LOCATION_CHARGE = new BigDecimal("100.00");

    /**
     * Gets all Reservation Objects
     *
     * @return List of all Reservations
     */
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationService::mapReservationToDTO)
                .toList();
    }

    /**
     * Maps a Reservation entity to a ReservationDTO (Data Transfer Object).
     *
     * @param reservation The Reservation entity to be mapped to a ReservationDTO.
     * @return A ReservationDTO representing the mapped data.
     */
    public static ReservationDTO mapReservationToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getReservationId(),
                reservation.getClient().getId(),
                reservation.getCar().getCarId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStartBranch().getBranchId(),
                reservation.getEndBranch().getBranchId()
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
     * associates the car and client with the reservation, calculates the price based on the reservation duration, updates
     * revenue total and handles potential conflicts with existing reservations and updates revenue for branch.
     *
     * @param reservationDto Object containing updated reservation dat
     * @param reservation The reservation object to be updated
     * @throws ObjectNotFoundInRepositoryException if no car or customer is found with the provided ID.
     * @throws ReservationTimeCollisionException if there are time collisions with existing reservations for the selected car
     */
    private void updateReservationDetails(ReservationDTO reservationDto, Reservation reservation) {
        if(reservationDto.startDate().isEqual(reservationDto.endDate())) {
            throw new ReservationTimeCollisionException("Car should be reserved for at least one day!");
        }

        Car carFromRepo = carRepository.findById(reservationDto.car_id())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under that ID"));

        Client clientFromRepo = clientRepository.findById(reservationDto.customer_id())
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No customer under that ID"));

        if (!carFromRepo.getReservations().isEmpty()) {
            List<DateTimePeriod> timeCollision = carFromRepo.getReservations().stream()
                    .map(resObject -> new DateTimePeriod(resObject.getStartDate(), resObject.getEndDate()))
                    .filter(dtp -> isDateSuitable(reservationDto, dtp))
                    .toList();
            if (!timeCollision.isEmpty()) {
                throw new ReservationTimeCollisionException("Car cannot be reserved for given time period!");
            }
        }

        reservation.setStartDate(reservationDto.startDate());
        reservation.setEndDate(reservationDto.endDate());
        setStartEndBranch(reservationDto, reservation);
        reservation.setCar(carFromRepo);
        reservation.setClient(clientFromRepo);

        checkBranchAvailability(reservation);

        long daysDifference = ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        BigDecimal price = carFromRepo.getPrice().multiply(BigDecimal.valueOf(daysDifference));

        if(!reservationDto.startBranchId().equals(reservationDto.endBranchId())) {
            price = price.add(CROSS_LOCATION_CHARGE);
        }
        reservation.setPrice(price);

        revenueService.updateRevenue(reservation.getCar().getBranch().getRevenue().getRevenueId(), price);
    }

    /**
     * Checks branch availability for the specified reservation to ensure there are no time collisions.
     * Throws a ReservationTimeCollisionException if a collision is detected.
     *
     * @param reservation The reservation to check for branch availability.
     * @throws ReservationTimeCollisionException If a time collision is detected.
     */
    private void checkBranchAvailability(Reservation reservation) {
        Reservation firstBefore = getFirstReservationPreviousTo(reservation);
        if (firstBefore != null &&
                !Objects.equals(reservation.getStartBranch().getBranchId(), firstBefore.getEndBranch().getBranchId()) &&
                Math.abs(ChronoUnit.DAYS.between(reservation.getStartDate(), firstBefore.getEndDate())) <= 1) {
            throw new ReservationTimeCollisionException("Car can be rented only from Branch #" +
                    firstBefore.getEndBranch().getBranchId() + " for the selected date!");
        }

        Reservation firstAfter = getFirstReservationAfter(reservation);
        if(firstAfter != null &&
        !reservation.getEndBranch().equals(firstAfter.getStartBranch()) &&
        Math.abs(ChronoUnit.DAYS.between(reservation.getEndDate(), firstAfter.getStartDate())) <= 1) {
            throw new ReservationTimeCollisionException("Car can be returned only to Branch #" +
                    firstAfter.getStartBranch().getBranchId() + " for the selected date!");
        }
    }

    /**
     * Retrieves the first reservation that ends before the start date of the specified reservation.
     *
     * @param reservation The reservation for which the previous reservation is sought.
     * @return The first reservation ending before the start date of the specified reservation,
     *         or {@code null} if no such reservation is found.
     */
    private Reservation getFirstReservationPreviousTo(Reservation reservation) {
        return reservationRepository.findAll().stream()
                .filter(filteredReservation -> filteredReservation.getEndDate().isBefore(reservation.getStartDate()))
                .min(Comparator.comparingLong(reservationBefore ->
                        ChronoUnit.DAYS.between(reservationBefore.getEndDate(), reservation.getStartDate()))).orElse(null);
    }

    /**
     * Retrieves the first reservation that starts after the end date of the specified reservation.
     *
     * @param reservation The reservation for which the subsequent reservation is sought.
     * @return The first reservation starting after the end date of the specified reservation,
     *         or {@code null} if no such reservation is found.
     */
    private Reservation getFirstReservationAfter(Reservation reservation) {
        return reservationRepository.findAll().stream()
                .filter(filteredReservation -> filteredReservation.getStartDate().isAfter(reservation.getEndDate()))
                .min(Comparator.comparingLong(reservationBefore ->
                        ChronoUnit.DAYS.between(reservationBefore.getEndDate(), reservation.getStartDate()))).orElse(null);
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
        Reservation foundReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No reservation under ID #" + id));
        reservationRepository.delete(foundReservation);
    }

    /**
     * Cancels a reservation identified by the provided ID and updates the revenue based on cancellation rules.
     * If the reservation is canceled more than or equal to 2 days before the rental date,
     * updates the revenue by negating the reservation price.
     * Otherwise, updates the revenue by negating 80% of the reservation price.
     *
     * @param id The ID of the reservation to be canceled.
     * @throws ObjectNotFoundInRepositoryException if no reservation is found for the provided ID.
     */
    @Transactional
    public void cancelReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No reservation under ID #" + id));

        if(reservation.getRent() != null) {
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), reservation.getRent().getRentDate()));
            if(daysBetween > 2) {
                revenueService.updateRevenue(reservation.getCar().getBranch().getRevenue().getRevenueId(), reservation.getPrice().negate());
            } else {
                revenueService.updateRevenue(reservation.getCar().getBranch().getRevenue().getRevenueId(), reservation.getPrice().negate().multiply(BigDecimal.valueOf(0.8)));
            }
        } else {
            revenueService.updateRevenue(reservation.getCar().getBranch().getRevenue().getRevenueId(), reservation.getPrice().negate());
        }
    }
}

record DateTimePeriod(LocalDate start, LocalDate end) {
}