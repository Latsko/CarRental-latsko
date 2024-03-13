package pl.sda.carrental.controller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.CarRental;
import pl.sda.carrental.model.DTO.CarDTO;
import pl.sda.carrental.service.BranchService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/branches")
public class BranchController {
    private final BranchService branchService;

    @GetMapping
    public List<BranchDTO> getBranches() {

        List<BranchDTO> list = new ArrayList<>();
        for (Branch branch : branchService.getAllBranches()) {
            BranchDTO branchDTO = mapToBranchDTO(branch);
            list.add(branchDTO);
        }
        return list;
    }

    @GetMapping("/{id}")
    public BranchDTO getById(@PathVariable Long id) {
        Branch branch = branchService.getById(id);
        return mapToBranchDTO(branch);
    }

    @GetMapping("/{id}/availableCarsOnDate/{date}")
    public List<CarDTO> getCarsAvailableOnDate(@PathVariable Long id, @PathVariable String date) {
        return branchService.getCarsAvailableAtBranchOnDate(id, date);
    }

    private BranchDTO mapToBranchDTO(Branch branch) {
        CarRental carRental = branch.getCarRental();

        if(branch.getCarRental() == null) {
            return new BranchDTO(
                    branch.getBranchId(),
                    branch.getName(),
                    null);
        }

        return new BranchDTO(
                branch.getBranchId(),
                branch.getName(),
                new HQDetails(
                        carRental.getName(),
                        carRental.getOwner(),
                        carRental.getDomain(),
                        carRental.getAddress()
                )
        );
    }

    @PostMapping
    public void addBranch(@RequestBody Branch branch) {
        branchService.addBranch(branch);
    }

    @PutMapping("/addCar/toBranchUnderId/{id}")
    public void addCarToBranch(@PathVariable Long id, @RequestBody Car car) {
        branchService.addCarToBranchByAccordingId(id, car);
    }

    @PutMapping("/{id}")
    public Branch modifyBranch(@PathVariable Long id, @RequestBody Branch branch) {
        return branchService.editBranch(id, branch);
    }

    @DeleteMapping("/{id}")
    public void removeBranch(@PathVariable Long id) {
        branchService.removeBranch(id);
    }

    @PatchMapping("/removeCar/{car_id}/fromBranch/{branch_id}")
    public void removeCarFromBranch(@PathVariable Long car_id, @PathVariable Long branch_id) {
        branchService.removeCarFromBranch(car_id, branch_id);
    }

    @PatchMapping("/assignCar/{car_id}/toBranch/{branch_id}")
    public void assignCarToBranch(@PathVariable Long car_id, @PathVariable Long branch_id) {
        branchService.assignCarToBranch(car_id, branch_id);
    }

    @PatchMapping("/removeEmployee/{employee_id}/fromBranch/{branch_id}")
    public void removeEmployeeFromBranch(@PathVariable Long employee_id, @PathVariable Long branch_id) {
        branchService.removeEmployeeFromBranch(employee_id, branch_id);
    }

    @PatchMapping("/assignEmployee/{employee_id}/toBranch/{branch_id}")
    public void assignEmployeeToBranch(@PathVariable Long employee_id, @PathVariable Long branch_id) {
        branchService.assignEmployeeToBranch(employee_id, branch_id);
    }

    @PatchMapping("/assignManager/{manager_id}/forBranch/{branch_id}")
    public void assignManagerForBranch(@PathVariable Long manager_id, @PathVariable Long branch_id) {
        branchService.addManagerForBranch(manager_id, branch_id);
    }

    @PatchMapping("/removeManagerFromBranch/{branch_id}")
    public void removeManagerFromBranch(@PathVariable Long branch_id) {
        branchService.removeManagerFromBranch(branch_id);
    }
}

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class HQDetails {
    private final String CarRentalName;
    private final String owner;
    private final String internetDomain;
    private final String address;
}

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
class BranchDTO {
    private final Long branchId;
    private final String branchName;
    private final HQDetails mainBranchDetail;

}
