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
    private Long reservationId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference(value = "clientReservation-reference")
    private Client client;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "car_id")
    @JsonBackReference(value = "carReservation-reference")
    private Car car;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @DecimalMin(value = "1.00", message = "Price must be greater than 1.00")
    @DecimalMax(value = "100000.00", message = "Price must be lesser than 100000.00")
    @Digits(integer = 7, fraction = 2, message = "Price must have up to 7 digits in total and 2 decimal places")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "start_branch_id")
    @JsonBackReference(value = "startBranch-reference")
    private Branch startBranch;

    @ManyToOne
    @JoinColumn(name = "end_branch_id")
    @JsonBackReference(value = "endBranch-reference")
    private Branch endBranch;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JoinColumn(name = "rent_id")
    @JsonBackReference(value = "reservationRent-reference")
    private Rent rent;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JoinColumn(name = "return_id")
    @JsonBackReference(value = "reservationReturnal-reference")
    private Returnal returnal;
}
