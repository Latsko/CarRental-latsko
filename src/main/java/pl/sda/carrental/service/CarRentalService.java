package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.BranchAlreadyOpenInCityException;
import pl.sda.carrental.exceptionHandling.CarRentalAlreadyExistsException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.CarRental;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRentalRepository;

@Service
@RequiredArgsConstructor
public class CarRentalService {
    private final CarRentalRepository carRentalRepository;
    private final BranchRepository branchRepository;

    /**
     * Retrieves the car rental company details.
     *
     * @return The CarRental object representing the car rental company.
     * @throws ObjectNotFoundInRepositoryException if no car rental company is found.
     */
    public CarRental getCarRental() {
        return carRentalRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("There is no car rental company"));
    }

    /**
     * Saves or updates the details of the car rental company.
     *
     * @param carRental The CarRental object representing the car rental company to be saved or updated.
     * @throws CarRentalAlreadyExistsException if there already is car rental in repository
     */
    public void saveCarRental(CarRental carRental) {
        if(!carRentalRepository.findAll().isEmpty()) {
            throw new CarRentalAlreadyExistsException("Car Rental already exists!");
        }
        carRentalRepository.save(carRental);
    }


    /**
     * Edits the details of the existing car rental company.
     *
     * @param carRental The CarRental object containing updated information to edit the car rental company.
     * @throws ObjectNotFoundInRepositoryException if there is no existing car rental company to edit.
     */
    public void editCarRental(CarRental carRental) {
        CarRental edited = carRentalRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("There is no car rental company to edit"));

        edited.setName(carRental.getName());
        edited.setAddress(carRental.getAddress());
        edited.setOwner(carRental.getOwner());
        edited.setLogo(carRental.getLogo());

        carRentalRepository.save(edited);
    }

    /**
     * Deletes the car rental company from the repository.
     *
     * @throws ObjectNotFoundInRepositoryException if there is no existing car rental company to delete.
     */
    public void deleteCarRental() {
        CarRental carRental = carRentalRepository.findAll().stream()
                .findFirst().orElseThrow(() ->
                        new ObjectNotFoundInRepositoryException("There is no car rental company"));

        carRental.getBranches().clear();

        carRentalRepository.delete(carRental);
    }

    /**
     * Opens a new branch for the car rental company.
     * Checks if a car rental company exists. If not, throws an exception.
     * Verifies if a branch with the same address already exists in the car rental company.
     * If a branch already exists in the same city, throws a BranchAlreadyOpenInCityException.
     * Otherwise, saves the new branch to the repository and associates it with the existing car rental company.
     *
     * @param branch The Branch object representing the new branch to be opened.
     * @throws ObjectNotFoundInRepositoryException     if the car rental company has not been created yet.
     * @throws BranchAlreadyOpenInCityException        if a branch with the same address is already open in the city.
     */
    public void openNewBranch(Branch branch) {
        CarRental carRental = carRentalRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Car Rental has not been created yet"));
        long branchAlreadyOpenInCity = carRental
                .getBranches()
                .stream()
                .map(Branch::getAddress)
                .filter(address -> address.equals(branch.getAddress()))
                .count();
        if(branchAlreadyOpenInCity > 0) {
            throw new BranchAlreadyOpenInCityException("Branch " + branch.getName() + " is already open in city "
            + branch.getAddress());
        }

        carRental.getBranches().add(branch);
        branch.setCarRental(carRental);

        branchRepository.save(branch);
        carRentalRepository.save(carRental);
    }

    /**
     * Deletes a branch identified by the provided ID.
     *
     * @param id The ID of the branch to be deleted.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided ID.
     */
    public void closeBranchUnderId(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under  ID #" + id));

        branch.getClients().clear();
        branch.getCars().clear();
        branch.getEmployees().clear();

        branchRepository.deleteById(id);
    }
}
