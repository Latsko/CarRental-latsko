package pl.sda.carrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import pl.sda.carrental.configuration.auth.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@Table(name = "returnals")
public class Returnal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Long returnId;
    private String comments;
    private LocalDate returnDate;

    @DecimalMin(value = "0.00", message = "Upcharge cannot be lower than 0.00")
    @DecimalMax(value = "10000.00", message = "Upcharge must be lesser than 10000.00")
    @Digits(integer = 9, fraction = 2, message = "Upcharge must have up to 7 digits in total and 2 decimal places")
    private BigDecimal upcharge;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    @JsonBackReference(value = "reservationReturnal-reference")
    private Reservation reservation;
}
