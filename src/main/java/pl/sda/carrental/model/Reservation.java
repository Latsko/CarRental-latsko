package pl.sda.carrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationId;// fixMe: nazwą pola powinna być nadana nazwa jako 'snakeCase'

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference(value = "clientReservation-reference") // wydaje się nadmiarowe
    private Client client;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonBackReference(value = "carReservation-reference")// wydaje się nadmiarowe
    private Car car;

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDate endDate;

    @DecimalMin(value = "1.00", message = "Price must be greater than 1.00")
    @DecimalMax(value = "100000.00", message = "Price must be lesser than 100000.00")
    @Digits(integer = 7, fraction = 2, message = "Price must have up to 7 digits in total and 2 decimal places")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "start_branch_id")
    @JsonBackReference(value = "startBranch-reference")// wydaje się nadmiarowe
    private Branch startBranch;

    @ManyToOne
    @JoinColumn(name = "end_branch_id")
    @JsonBackReference(value = "endBranch-reference")// wydaje się nadmiarowe
    private Branch endBranch;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JsonBackReference(value = "reservationRent-reference") // powinno byc po stronie DTO
    private Rent rent;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JsonBackReference(value = "reservationReturnal-reference") // powinno byc po stronie DTO
    private Returnal returnal;
}
