package pl.sda.carrental.model.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RentDTO(@NotNull Long employeeId,
                      String comments,
                      @NotNull LocalDate rentDate,
                      @NotNull Long reservationId) {}
