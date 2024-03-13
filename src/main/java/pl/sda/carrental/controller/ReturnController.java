package pl.sda.carrental.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.DTO.ReturnDTO;
import pl.sda.carrental.model.Returnal;
import pl.sda.carrental.service.ReturnService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/returnals")
public class ReturnController {
    private final ReturnService returnService;

    @GetMapping
    public List<Returnal> getReturnals() {
        return returnService.getAllReturnals();
    }

    @PostMapping
    public Returnal saveReturnal(@RequestBody @Valid ReturnDTO returnDTO) {
        return returnService.saveReturn(returnDTO);
    }

    @PutMapping("/{id}")
    public Returnal editReturnal(@PathVariable Long id, @RequestBody ReturnDTO returnDTO) {
        return returnService.editReturnal(id, returnDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteReturnal(@PathVariable Long id) {
        returnService.deleteReturnal(id);
    }
}
