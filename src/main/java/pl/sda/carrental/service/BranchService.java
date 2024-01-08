package pl.sda.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.ObjectAlreadyAssignedToBranchException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.CarRepository;
import pl.sda.carrental.repository.EmployeeRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Adds a new branch to the repository.
     *
     * @param branch The Branch object to be added.
     */
    public void addBranch(Branch branch) {
        if(!branchRepository.findAll().isEmpty()) {
            throw new ObjectAlreadyAssignedToBranchException("Rent already exists!");
        }
        branchRepository.save(branch);
    }

    /**
     * Retrieves a list of all branches stored in the repository.
     *
     * @return A list of Branch objects.
     */
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    /**
     * Removes a branch based on the provided ID.
     * Retrieves the branch by ID from the repository and deletes it.
     *
     * @param id The ID of the branch to be removed.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided ID.
     */
    public void removeBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under  ID #" + id));

        branch.getClients().clear();
        branch.getCars().clear();
        branch.getEmployees().clear();

        branchRepository.deleteById(id);
    }

    /**
     * Edits the details of an existing branch based on the provided ID.
     * Retrieves the branch by ID from the repository and updates its address and name.
     * Deletes the existing branch using the provided ID and saves the updated branch details.
     *
     * @param id     The ID of the branch to be edited.
     * @param branch The updated Branch object with new details.
     * @return The edited Branch object after updating the details.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided ID.
     */
    public Branch editBranch(Long id, Branch branch) {
        Branch found = branchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under  ID #" + id));

        found.setAddress(branch.getAddress());
        found.setName(branch.getName());

        branchRepository.deleteById(id);

        return branchRepository.save(found);
    }

    /**
     * Retrieves a branch by the provided ID.
     *
     * @param id The ID of the branch to retrieve.
     * @return The retrieved Branch object.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided ID.
     */
    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + id));
    }

    /**
     * Adds a car to a branch identified by the provided branch ID.
     * Checks if any branches exist before adding the car. If no branches exist, it throws an exception.
     * Retrieves the branch by ID from the repository and assigns the provided car to it.
     *
     * @param id  The ID of the branch to which the car will be added.
     * @param car The Car object to be added.
     * @throws ObjectNotFoundInRepositoryException if no branches exist or if no branch is found under the provided ID.
     */
    public void addCarToBranchByAccordingId(Long id, Car car) {
        if (branchRepository.findAll().isEmpty()) {
            throw new ObjectNotFoundInRepositoryException("There are no created branches currently");
        }

        Branch foundBranch = branchRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + id));

        car.setBranch(foundBranch);
        carRepository.save(car);
        foundBranch.getCars().add(car);
    }

    /**
     * Removes a car from a branch based on the provided car ID and branch ID.
     * Retrieves the branch by ID from the repository and finds the car within its assigned cars.
     * If the car is found, it is disassociated from the branch and removed.
     *
     * @param carId    The ID of the car to be removed from the branch.
     * @param branchId The ID of the branch from which the car will be removed.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided branch ID or if the specified car is not assigned to that branch.
     */
    public void removeCarFromBranch(Long carId, Long branchId) {
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));

        Car foundCar = foundBranch.getCars().stream()
                .filter(car -> Objects.equals(car.getCar_id(), carId))
                .findFirst()
                .orElseThrow(() ->
                        new ObjectNotFoundInRepositoryException("No car under ID #"
                                + carId + " is assigned to branch under ID #" + branchId));

        foundBranch.getCars().remove(foundCar);
        foundCar.setBranch(null);

        branchRepository.save(foundBranch);
        carRepository.save(foundCar);
    }

    /**
     * Assigns a car to a branch based on the provided car ID and branch ID.
     * Retrieves the car by ID from the repository and checks if it is already assigned to a branch.
     * If the car is not yet assigned to any branch, it associates it with the specified branch.
     *
     * @param carId    The ID of the car to be assigned to the branch.
     * @param branchId The ID of the branch to which the car will be assigned.
     * @throws ObjectNotFoundInRepositoryException      if no car is found under the provided car ID or if no branch is found under the provided branch ID.
     * @throws ObjectAlreadyAssignedToBranchException   if the car is already assigned to an existing branch.
     */
    public void assignCarToBranch(Long carId, Long branchId) {
        Car foundCar = carRepository.findById(carId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under ID #" + carId));
        if(foundCar.getBranch() != null) {
            throw new ObjectAlreadyAssignedToBranchException("Car already assigned to existing branch!");
        }
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));

        foundBranch.getCars().add(foundCar);
        foundCar.setBranch(foundBranch);

        branchRepository.save(foundBranch);
        carRepository.save(foundCar);
    }

    /**
     * Assigns an employee to a branch based on the provided employee ID and branch ID.
     * Retrieves the employee by ID from the repository and verifies if they are already assigned to a branch.
     * If the employee is not yet assigned to any branch, associates them with the specified branch.
     *
     * @param employeeId The ID of the employee to be assigned to the branch.
     * @param branchId   The ID of the branch to which the employee will be assigned.
     * @throws ObjectNotFoundInRepositoryException    if no employee is found under the provided employee ID or if no branch is found under the provided branch ID.
     * @throws ObjectAlreadyAssignedToBranchException if the employee is already assigned to an existing branch.
     */
    public void assignEmployeeToBranch(Long employeeId, Long branchId) {
        Employee foundEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under ID #" + employeeId));
        if(foundEmployee.getBranch() != null) {
            throw new ObjectAlreadyAssignedToBranchException("Employee already assigned to existing branch!");
        }
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));

        foundBranch.getEmployees().add(foundEmployee);
        foundEmployee.setBranch(foundBranch);

        branchRepository.save(foundBranch);
        employeeRepository.save(foundEmployee);
    }

    /**
     * Removes an employee from a branch based on the provided employee ID and branch ID.
     * Retrieves the branch by ID from the repository and finds the employee within its assigned employees.
     * If the employee is found, they are disassociated from the branch and removed.
     *
     * @param employeeId The ID of the employee to be removed from the branch.
     * @param branchId   The ID of the branch from which the employee will be removed.
     * @throws ObjectNotFoundInRepositoryException if no branch is found under the provided branch ID or if the specified employee is not assigned to that branch.
     */
    public void removeEmployeeFromBranch(Long employeeId, Long branchId) {
        Branch foundBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No branch under ID #" + branchId));

        Employee foundEmployee = foundBranch.getEmployees().stream()
                .filter(employee -> Objects.equals(employee.getEmployee_id(), employeeId))
                .findFirst()
                .orElseThrow(() ->
                        new ObjectNotFoundInRepositoryException("No employee under ID #"
                                + employeeId + " is assigned to branch under ID #" + branchId));

        foundBranch.getEmployees().remove(foundEmployee);
        foundEmployee.setBranch(null);

        branchRepository.save(foundBranch);
        employeeRepository.save(foundEmployee);
    }

}
