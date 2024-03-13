package pl.sda.carrental.service;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.carrental.exceptionHandling.ObjectAlreadyAssignedToBranchException;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.DTO.CarDTO;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.model.enums.Position;
import pl.sda.carrental.repository.*;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final CarRepository carRepository;
    private final EmployeeRepository employeeRepository;
    private final ReservationRepository reservationRepository;
    private final CarRentalRepository carRentalRepository;

    /**
     * Adds a new branch to the repository.
     *
     * @param branch The Branch object to be added.
     */
    @Transactional
    public Branch addBranch(Branch branch) {

        if (carRentalRepository.findAll().stream().findFirst().isPresent()) {
            branch.setCarRental(carRentalRepository.findAll().stream().findFirst().get());
        } else {
            throw new ObjectNotFoundInRepositoryException("No Car Rental for branch to be assigned to");
        }

        Employee potentialManager;
        if (branch.getManagerId() != null) {
            potentialManager = employeeRepository.findById(branch.getManagerId())
                    .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Cannot find employee to assign as Manager!"));

            potentialManager.setPosition(Position.MANAGER);
            potentialManager.setBranch(branch);
            employeeRepository.save(potentialManager);
        }
        return branchRepository.save(branch);
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
    @Transactional
    public void removeBranch(Long id) {
        Branch branch = getById(id);
        List<Reservation> reservationsWithThisBranch = new ArrayList<>();
        for (Reservation reservation : reservationRepository.findAll()) {
            if (reservation.getStartBranch().getBranchId().equals(id) ||
                    reservation.getEndBranch().getBranchId().equals(id)) {
                reservationsWithThisBranch.add(reservation);
            }
        }

        reservationRepository.deleteAll(reservationsWithThisBranch);

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
    @Transactional
    public Branch editBranch(Long id, Branch branch) {
        Branch found = getById(id);
        found.setAddress(branch.getAddress());
        found.setName(branch.getName());

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
    @Transactional
    public Car addCarToBranchByAccordingId(Long id, Car car) {
        if (branchRepository.findAll().isEmpty()) {
            throw new ObjectNotFoundInRepositoryException("There are no created branches currently");
        }

        Branch foundBranch = getById(id);
        car.setBranch(foundBranch);
        foundBranch.getCars().add(car);
        return carRepository.save(car);
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
    @Transactional
    public void removeCarFromBranch(Long carId, Long branchId) {
        Branch foundBranch = getById(branchId);
        Car foundCar = foundBranch.getCars().stream()
                .filter(car -> Objects.equals(car.getCarId(), carId))
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
     * @throws ObjectNotFoundInRepositoryException    if no car is found under the provided car ID or if no branch is found under the provided branch ID.
     * @throws ObjectAlreadyAssignedToBranchException if the car is already assigned to an existing branch.
     */
    @Transactional
    public void assignCarToBranch(Long carId, Long branchId) {
        Car foundCar = carRepository.findById(carId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No car under ID #" + carId));
        if (foundCar.getBranch() != null) {
            throw new ObjectAlreadyAssignedToBranchException("Car already assigned to existing branch!");
        }
        Branch foundBranch = getById(branchId);
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
    @Transactional
    public void assignEmployeeToBranch(Long employeeId, Long branchId) {
        Employee foundEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under ID #" + employeeId));
        if (foundEmployee.getBranch() != null) {
            throw new ObjectAlreadyAssignedToBranchException("Employee already assigned to existing branch!");
        }
        Branch foundBranch = getById(branchId);
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
    @Transactional
    public void removeEmployeeFromBranch(Long employeeId, Long branchId) {
        Branch foundBranch = getById(branchId);
        Employee foundEmployee = foundBranch.getEmployees().stream()
                .filter(employee -> Objects.equals(employee.getEmployeeId(), employeeId))
                .findFirst()
                .orElseThrow(() ->
                        new ObjectNotFoundInRepositoryException("No employee under ID #"
                                + employeeId + " is assigned to branch under ID #" + branchId));

        foundBranch.getEmployees().remove(foundEmployee);
        foundEmployee.setBranch(null);

        branchRepository.save(foundBranch);
        employeeRepository.save(foundEmployee);
    }

    /**
     * Assigns a manager to a branch based on their respective unique identifiers.
     *
     * @param managerId The unique identifier of the employee to be assigned as the manager.
     * @param branchId  The unique identifier of the branch to which the manager will be assigned.
     * @throws ObjectAlreadyAssignedToBranchException If the branch already has an assigned manager.
     * @throws ObjectNotFoundInRepositoryException    If no employee is found with the given manager ID.
     */
    @Transactional
    public void addManagerForBranch(Long managerId, Long branchId) {
        Branch branch = getById(branchId);
        if (branch.getManagerId() != null) {
            throw new ObjectAlreadyAssignedToBranchException("Branch already has Manager!");
        }
        Employee potentialManager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("Cannot find employee to assign as Manager!"));

        branch.setManagerId(managerId);
        potentialManager.setPosition(Position.MANAGER);
        potentialManager.setBranch(branch);

        employeeRepository.save(potentialManager);
        branchRepository.save(branch);
    }

    /**
     * Removes the manager assignment from a branch based on its unique identifier.
     *
     * @param branchId The unique identifier of the branch from which the manager will be removed.
     * @throws ObjectNotFoundInRepositoryException If the branch does not have any assigned manager.
     */
    @Transactional
    public void removeManagerFromBranch(Long branchId) {
        Branch branch = getById(branchId);
        if (branch.getManagerId() == null) {
            throw new ObjectNotFoundInRepositoryException("Branch does not have any assigned Manager!");
        }

        branch.setManagerId(null);
        branchRepository.save(branch);
    }

    /**
     * Retrieves a list of available cars at a specific branch on a given date, considering reservations and returnals.
     * Combines available cars at the given branch and cars with returnals at the specified branch.
     *
     * @param id   The ID of the branch to check for available cars.
     * @param date The date for which car availability is checked (in the format "yyyy-MM-dd").
     * @return A list of CarDTO objects representing available cars at the specified branch on the given date.
     */
    public List<CarDTO> getCarsAvailableAtBranchOnDate(Long id, String date) {
        Branch foundBranch = getById(id);
        List<Car> list = new ArrayList<>();
        for (Car car1 : foundBranch.getCars()) {
            if (isDateAvailableForCar(car1, LocalDate.parse(date))) {
                list.add(car1);
            }
        }
        List<Car> availableCarsAtGivenBranch = new ArrayList<>(list);

        List<Car> carsFromOtherBranches = new ArrayList<>();
        for (Branch branch : branchRepository.findAll()) {
            carsFromOtherBranches.addAll(branch.getCars());
        }

        List<Car> carsWithReturnAtBranch = new ArrayList<>();
        for (Car car : carsFromOtherBranches) {
            if (hasReturnalsAtBranchOnDatePrior(id, car, LocalDate.parse(date))) {
                carsWithReturnAtBranch.add(car);
            }
        }

        availableCarsAtGivenBranch.addAll(carsWithReturnAtBranch);
        Set<Car> uniqueCars = new HashSet<>(availableCarsAtGivenBranch);

        List<CarDTO> result = new ArrayList<>();
        for (Car uniqueCar : uniqueCars) {
            CarDTO carDTO = CarService.mapCarToCarDTO(uniqueCar);
            result.add(carDTO);
        }
        return new ArrayList<>(result);
    }

    /**
     * Checks if a car has returnals at a specific branch on a day before given date.
     *
     * @param id   The branch ID to check for returnals.
     * @param car  The car to check for returnals.
     * @param date The date to check for returnals.
     * @return {@code true} if returnals exist at the specified branch on the given date; {@code false} otherwise.
     */
    private boolean hasReturnalsAtBranchOnDatePrior(Long id, Car car, LocalDate date) {
        return car.getReservations().stream()
                .filter(reservation -> reservation.getEndBranch().getBranchId().equals(id))
                .anyMatch(reservation -> reservation.getEndDate().plusDays(1).equals(date));
    }

    /**
     * Checks if a given date is available for a specific car by examining its reservations.
     *
     * @param car  The car to check availability for.
     * @param date The date to check for availability.
     * @return {@code true} if the date is available for the car; {@code false} otherwise.
     */
    private boolean isDateAvailableForCar(Car car, LocalDate date) {
        return car.getReservations().stream()
                .noneMatch(reservation ->
                        (reservation.getStartDate().isBefore(date) ||
                                reservation.getStartDate().equals(date)) &&
                                (reservation.getEndDate().isAfter(date) ||
                                        reservation.getEndDate().equals(date)));
    }
}
