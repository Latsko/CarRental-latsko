package pl.sda.carrental.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.Car;
import pl.sda.carrental.model.enums.Status;
import pl.sda.carrental.service.CarService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping("/{id}")
    public Car getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @GetMapping
    public List<Car> getCars(){
        return carService.getCars();
    }

    @GetMapping("/statusOnDate/{id}") Status getCarStatusOnDate(@PathVariable Long id, LocalDate date) {
        return carService.getStatusOnDateForCarUnderId(id, date);
    }

    @PostMapping
    public Car addCar(@RequestBody @Valid Car car) {
        return carService.addCar(car);
    }

    @PutMapping("/{id}")
    public void editCar(@PathVariable Long id, @RequestBody @Valid Car car) {
        carService.editCar(id, car);
    }

    @PatchMapping("/setMileageAndPrice/{id}")
    public void updateMileageAndPrice(@RequestParam double mileage, @RequestParam BigDecimal price, @PathVariable Long id) {
        carService.updateMileageAndPrice(mileage, price, id);
    }

    @PatchMapping("/setStatus/{id}")
    public void updateStatus(@RequestParam String status, @PathVariable Long id) {
        carService.updateStatus(status, id);
    }
    @DeleteMapping("/{id}")
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCarById(id);
    }
}
