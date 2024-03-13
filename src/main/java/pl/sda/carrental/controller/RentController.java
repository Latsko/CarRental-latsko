package pl.sda.carrental.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.DTO.RentDTO;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.service.RentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rents")
public class RentController {
    private final RentService rentService;

    @GetMapping
    public List<Rent> getAllRents() {
        return rentService.allRents();
    }

    @PostMapping
    public Rent save(@RequestBody @Valid RentDTO rent) {
        return rentService.save(rent);
    }

    @PutMapping("/{id}")
    public Rent editRent(@PathVariable Long id, @RequestBody RentDTO rent) {
        return rentService.editRent(id, rent);
    }

    @DeleteMapping("/{id}")
    public void deleteRent(@PathVariable Long id) {
        rentService.deleteRentById(id);
    }
}
