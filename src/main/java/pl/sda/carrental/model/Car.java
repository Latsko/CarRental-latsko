package pl.sda.carrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;
import pl.sda.carrental.model.enums.Status;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;
    private String make;
    @Getter
    private String model;

    @Column(name = "body_style")
    private String bodyStyle;
    @Column(name = "year_of_manufacture")
    private int yearOfManufacture;
    private String colour;
    private double mileage;
    private Status status;
    @DecimalMin(value = "1.00", message = "Price must be grater than 1.00")
    @DecimalMax(value = "10000.00", message = "Price must be lesser than 10000.00")
    @Digits(integer = 9, fraction = 2, message = "Price must have up to 7 digits in total and 2 decimal places")
    private BigDecimal price;

    @Schema(hidden = true)
    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference(value = "car-reference")
    private Branch branch;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "carReservation-reference")
    private Set<Reservation> reservations = new HashSet<>();

}
