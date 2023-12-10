package pl.sda.carrental.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car_rental")
public class CarRentalModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long car_rental_id;
    @NotNull(message = "name field cannot be null")
    private String name;
    @NotNull(message = "Internet Domain field cannot be null")
    private String domain;
    private String address;
    private String owner;
    private String logo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_rental_id")
    private Set<BranchModel> branches = new HashSet<>();
}
