package pl.sda.carrental.model.DTO;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReturnDTO(@NotNull Long employee,
                        String comments,
                        @NotNull LocalDate returnDate,
                        BigDecimal upcharge,
                        @NotNull Long reservationId) {
}
