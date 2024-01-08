package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRepository;
import pl.sda.carrental.repository.RentRepository;
import pl.sda.carrental.repository.ReturnRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final BranchRepository branchRepository;
    private final RentRepository rentRepository;
    private final ReturnRepository returnRepository;

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
    public void addCar(Car car) {
        carRepository.save(car);
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
    public void editCar(Long id, Car car) {
        Car childCar = carRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under ID #" + id));
        Branch parentBranch = childCar.getBranch();

        if(parentBranch != null) {
            Car editedCar = parentBranch.getCars().stream()
                    .filter(filteredCar -> filteredCar.equals(childCar))
                    .findFirst().orElseThrow(() ->
                            new ObjectNotFoundInRepositoryException("No car under ID #" +
                                    id + " in that branch"));
            editedCar.setCar_id(id);
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
    public void deleteCarById(Long id) {
        carRepository.findById(id).orElseThrow(() ->
                new ObjectNotFoundInRepositoryException("No car under ID #" + id));

        carRepository.deleteById(id);
    }
}
