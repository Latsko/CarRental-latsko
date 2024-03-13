package pl.sda.carrental.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.Branch;
import pl.sda.carrental.model.CarRental;
import pl.sda.carrental.service.CarRentalService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carRental")
public class CarRentalController {
    private final CarRentalService carRentalService;

    @GetMapping
    public CarRental getCarRental(){
        return carRentalService.getCarRental();
    }
    @PostMapping
    public void addCarRental(@RequestBody @Valid CarRental carRental){
        carRentalService.saveCarRental(carRental);
    }

    @PostMapping("/addBranch")
    public void openBranch(@RequestBody @Valid Branch branch) {
        carRentalService.openNewBranch(branch);
    }

    @PutMapping
    public void editCarRental(@RequestBody CarRental carRental){
        carRentalService.editCarRental(carRental);
    }

    @DeleteMapping
    public void deleteCarRental(){
        carRentalService.deleteCarRental();
    }

    @DeleteMapping("/deleteBranch/{id}")
    public void closeBranch(@PathVariable Long id) {
        carRentalService.closeBranchUnderId(id);
    }
}
