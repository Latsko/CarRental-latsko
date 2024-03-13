package pl.sda.carrental.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.IllegalArgumentForStatusException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.DTO.CarDTO;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.model.enums.Status;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final BranchRepository branchRepository;

    /**
     * Retrieves a car based on the provided ID.
     *
     * @param id The ID of the car to retrieve.
     * @return The Car object corresponding to the provided ID.
     * @throws ObjectNotFoundInRepositoryException if no car is found with the selected ID.
     */
    public Car getCarById(Long id) {
        return carRepository.findById(id).
                orElseThrow(() -> new ObjectNotFoundInRepositoryException("There is no car with selected id"));
    }

    /**
     * Retrieves a list of all cars.
     *
     * @return A list containing all Car objects available in the repository.
     */
    public List<Car> getCars() {
        return carRepository.findAll();
    }

    /**
     * Adds a new car to the repository.
     *
     * @param car The Car object representing the new car to be added.
     */
    @Transactional
    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    /**
     * Edits the details of a car identified by the provided ID.
     * Finds car with provided ID and parent branch. Then found Car is retrieved
     * from parent branch and modified according to given parameter.
     * Parent and child object are saved to appropriate repositories in order
     * to maintain all changes.
     *
     * @param id  The ID of the car to be edited.
     * @param car The Car object containing updated information for the car.
     * @throws ObjectNotFoundInRepositoryException if no car is found under the provided ID or if the car is not associated with a branch.
     */
    @Transactional
    public void editCar(Long id, Car car) {
        Car childCar = getCarById(id);
        Branch parentBranch = childCar.getBranch();

        if (parentBranch != null) {
            Car editedCar = parentBranch.getCars().stream()
                    .filter(filteredCar -> filteredCar.equals(childCar))
                    .findFirst().orElseThrow(() ->
                            new ObjectNotFoundInRepositoryException("No car under ID #" +
                                    id + " in that branch"));
            editedCar.setCarId(id);
            editedCar.setMake(car.getMake());
            editedCar.setModel(car.getModel());
            editedCar.setBodyStyle(car.getBodyStyle());
            editedCar.setYear(car.getYear());
            editedCar.setColour(car.getColour());
            editedCar.setMileage(car.getMileage());
            editedCar.setStatus(car.getStatus());
            editedCar.setPrice(car.getPrice());

            branchRepository.save(parentBranch);
            carRepository.save(editedCar);
        }
    }

    /**
     * Deletes a car and its associated data identified by the provided ID.
     * Deletes all associated rents and returns related to the car before removing the car itself
     * in order to maintain logic integrity and prevent SQL foreign key constraint violation.
     *
     * @param id The ID of the car to be deleted.
     * @throws ObjectNotFoundInRepositoryException if no car is found under the provided ID.
     */
    @Transactional
    public void deleteCarById(Long id) {
        getCarById(id);
        carRepository.deleteById(id);
    }

    /**
     * Updates the mileage and price of a car identified by the provided ID.
     *
     * @param mileage The new mileage value to set for the car.
     * @param price   The new price to set for the car.
     * @param id      The ID of the car to be updated.
     */
    @Transactional
    public void updateMileageAndPrice(double mileage, BigDecimal price, Long id) {
        Car foundCar = getCarById(id);
        foundCar.setMileage(mileage);
        foundCar.setPrice(price);

        carRepository.save(foundCar);
    }

    /**
     * Updates the status of a car identified by the provided ID.
     *
     * @param status The new status value to set for the car.
     * @param id     The ID of the car to be updated.
     * @throws IllegalArgumentForStatusException if the provided status does not match any predefined car status.
     */
    @Transactional
    public void updateStatus(String status, Long id) {
        Car foundCar = getCarById(id);

        if (!checkIfStatusExists(status)) {
            throw new IllegalArgumentForStatusException("Wrong argument for car Status!");
        }
        foundCar.setStatus(Status.valueOf(status));

        carRepository.save(foundCar);
    }

    /**
     * Checks if the provided string matches any of the predefined enum constants in Status.
     *
     * @param status The string representation of the status to be checked.
     * @return {@code true} if the provided status matches any enum constant, {@code false} otherwise.
     */
    private boolean checkIfStatusExists(String status) {
        for (Status checkedStatus : Status.values()) {
            if (String.valueOf(checkedStatus).equals(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the status of a car identified by the provided ID on a given date.
     * If the car is rented on the specified date, it returns the "RENTED" status,
     * otherwise returns the current status of the car.
     *
     * @param id   The ID of the car to retrieve the status for.
     * @param date The date for which the status is to be checked.
     * @return The status of the car on the specified date.
     */
    public Status getStatusOnDateForCarUnderId(Long id, LocalDate date) {
        Car foundCar = getCarById(id);
        long count = foundCar.getReservations().stream()
                .filter(reservation -> checkIfRentedOnDate(reservation, date))
                .count();

        if (count > 0) {
            return Status.RENTED;
        } else {
            return foundCar.getStatus();
        }
    }

    /**
     * Checks if a reservation covers the specified date.
     *
     * @param reservation The reservation to check.
     * @param date        The date to check if it falls within the reservation period.
     * @return {@code true} if the date is within the reservation period, {@code false} otherwise.
     */
    private boolean checkIfRentedOnDate(Reservation reservation, LocalDate date) {
        return reservation.getStartDate().equals(date) ||
                reservation.getEndDate().equals(date) ||
                (reservation.getStartDate().isBefore(date) && reservation.getEndDate().isAfter(date));
    }

    /**
     * Maps a Car entity to a CarDTO (Data Transfer Object).
     *
     * @param car The Car entity to be mapped to a CarDTO.
     * @return A CarDTO representing the mapped data.
     */
    public static CarDTO mapCarToCarDTO(Car car) {
        return new CarDTO(car.getMake(),
                car.getModel(),
                car.getBodyStyle(),
                car.getYear(),
                car.getColour(),
                car.getMileage(),
                car.getPrice());
    }
}
