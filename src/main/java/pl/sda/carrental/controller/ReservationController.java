package pl.sda.carrental.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.sda.carrental.model.DTO.ReservationDTO;
import pl.sda.carrental.model.Reservation;
import pl.sda.carrental.service.ReservationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationDTO> getReservations() {
        return reservationService.getAllReservations();
    }

    @PostMapping
    public Reservation saveReservation(@RequestBody @Valid ReservationDTO reservation) {
        return reservationService.saveReservation(reservation);
    }

    @PutMapping("/{id}")
    public Reservation editReservation(@PathVariable Long id, @RequestBody ReservationDTO reservationDTO) {
        return reservationService.editReservation(id, reservationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
    }

    @PatchMapping("/{id}")
    public void cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservationById(id);
    }
}
