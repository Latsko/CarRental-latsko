package pl.sda.carrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
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
    @JsonBackReference(value = "startBranch-reference")
    private Branch startBranch;

    @ManyToOne
    @JoinColumn(name = "end_branch_id")
    @JsonBackReference(value = "endBranch-reference")
    private Branch endBranch;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JsonBackReference(value = "reservationRent-reference")
    private Rent rent;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.REMOVE)
    @JsonBackReference(value = "reservationReturnal-reference")
    private Returnal returnal;
}
