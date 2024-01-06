package pl.sda.carrental.model;

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
@Table(name = "return_process")
public class Returnal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long return_id;
    private String comments;
    private LocalDate returnDate;

    @DecimalMin(value = "0.00", message = "Upcharge cannot be lower than 0.00")
    @DecimalMax(value = "10000.00", message = "Upcharge must be lesser than 10000.00")
    @Digits(integer = 9, fraction = 2, message = "Upcharge must have up to 7 digits in total and 2 decimal places")
    private BigDecimal upcharge;

    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @NotNull
    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
