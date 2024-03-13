package pl.sda.carrental.model.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class ReturnDTO {
    @NotNull
    private final Long employee;
    private final String comments;
    @NotNull
    private final LocalDate returnDate;
    private final BigDecimal upcharge;
    @NotNull
    private final Long reservationId;
}