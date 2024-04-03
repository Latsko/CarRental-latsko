package pl.sda.carrental.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.Employee;
import pl.sda.carrental.service.EmployeeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/manageL1/employees")
    public List<Employee> getEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping("/manageL1/employees")
    public Employee addEmployee(@RequestBody Employee employee) {
        return employeeService.addEmployee(employee);
    }

    @PutMapping("/manageL1/employees/{id}")
    public Employee editEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.editEmployee(id, employee);
    }

    @DeleteMapping("/manageL1/employees/{id}")
    public void deleteEmployee(@PathVariable Long id) {
            employeeService.deleteEmployee(id);
    }

}
