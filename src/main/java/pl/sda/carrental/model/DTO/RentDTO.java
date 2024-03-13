package pl.sda.carrental.model.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class RentDTO {
    @NotNull
    private final Long employeeId;
    private final String comments;
    @NotNull
    private final LocalDate rentDate;
    @NotNull
    private final Long reservationId;
}
