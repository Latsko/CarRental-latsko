package pl.sda.carrental.model.DTO;

import javax.validation.constraints.NotNull;

import lombok.*;
import pl.sda.carrental.model.Rent;
import pl.sda.carrental.model.Returnal;

import java.time.LocalDate;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@With
public class ReservationDTO {
    private final Long reservation_id;
    @NotNull
    private final Long customer_id;
    @NotNull
    private final Long car_id;
    @NotNull
    private final LocalDate startDate;
    @NotNull
    private final LocalDate endDate;
    @NotNull
    private final Long startBranchId;
    @NotNull
    private final Long endBranchId;
    private final Rent rent;
    private final Returnal returnal;
}
