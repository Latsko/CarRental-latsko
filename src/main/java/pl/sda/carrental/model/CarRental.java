package pl.sda.carrental.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car_rental")
@With
public class CarRental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_rental_id")
    private Long carRentalId;
    @NotNull(message = "name field cannot be null")
    private String name;
    @NotNull(message = "Internet Domain field cannot be null")
    private String domain;
    private String address;
    private String owner;
    private String logo;

    @OneToMany(mappedBy = "carRental", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Branch> branches = new HashSet<>();
}
