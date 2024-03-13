package pl.sda.carrental.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@With
@Table(name = "client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "surname cannot be null")
    private String surname;
    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "address cannot be null")
    private String address;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference(value = "client-reference")
    private Branch branch;
}
