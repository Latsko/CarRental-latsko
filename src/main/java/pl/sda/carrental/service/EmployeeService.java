package pl.sda.carrental.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sda.carrental.configuration.auth.model.Employee;
import pl.sda.carrental.configuration.auth.repository.EmployeeRepository;
import pl.sda.carrental.configuration.auth.repository.UserRepository;
import pl.sda.carrental.configuration.auth.util.LoginUtils;
import pl.sda.carrental.exceptionHandling.ObjectNotFoundInRepositoryException;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.model.Returnal;
import pl.sda.carrental.repository.BranchRepository;
import pl.sda.carrental.repository.RentRepository;
import pl.sda.carrental.repository.ReturnRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RentRepository rentRepository;
    private final ReturnRepository returnRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Gets all Employees objects
     *
     * @return List of all Employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * The editEmployee method is a transactional operation that allows for the modification of an existing employee based on the provided
     * employee ID and updated employee details. It retrieves the employee by ID from the repository, updates its details, deletes the
     * existing employee, and then saves the modified employee back to the repository
     *
     * @param id       The identifier of the employee to be edited
     * @param employee An object containing updated employee data
     * @return The modified employee object
     * @throws ObjectNotFoundInRepositoryException if no employee is found with the provided ID
     */
    @Transactional
    public Employee editEmployee(Long id, Employee employee) {
        Employee childEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under ID #" + id));
        Branch parentBranch = childEmployee.getBranch();

        LoginUtils.checkDuplicateLogin(employee.getLogin(), userRepository);

        childEmployee.setLogin(employee.getLogin());
        childEmployee.setPassword(passwordEncoder.encode(employee.getPassword()));
        childEmployee.setName(employee.getName());
        childEmployee.setSurname(employee.getSurname());
        childEmployee.setPosition(employee.getPosition());

        if(parentBranch != null) {
            parentBranch.getEmployees().stream()
                    .filter(filteredEmployee -> filteredEmployee.equals(childEmployee))
                    .findFirst()
                    .orElseThrow(() ->
                            new ObjectNotFoundInRepositoryException("No employee under ID #" +
                                    id + " in that branch"));
            branchRepository.save(parentBranch);
        }
        return employeeRepository.save(childEmployee);
    }

    /**
     * Deletes an employee by their unique ID.
     * Removes associations between the employee and related rents and returnals, if any,
     * and deletes the employee from the repository.
     *
     * @param id The unique identifier of the employee to be deleted.
     * @throws ObjectNotFoundInRepositoryException if no employee is found under the provided ID.
     *                                             If no employee is found, the deletion process fails.
     */
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("No employee under that ID!"));
        List<Rent> rentsWithEmployeeById = rentRepository.findRentsByEmployeeId(id);
        List<Returnal> returnsWithEmployeeById = returnRepository.findReturnalsByEmployeeId(id);

        for (Rent rent : rentsWithEmployeeById) {
            rent.setEmployee(null);
            rentRepository.save(rent);
        }

        for (Returnal returnal : returnsWithEmployeeById) {
            returnal.setEmployee(null);
            returnRepository.save(returnal);
        }

        employeeRepository.deleteById(id);
    }
}
