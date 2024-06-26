package pl.sda.carrental.configuration.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import pl.sda.carrental.model.Branch;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@TableGenerator(
        name = "tableGen",
        table = "ID_GEN",
        pkColumnName = "GEN_NAME",
        valueColumnName = "GEN_VAL",
        pkColumnValue = "USER_ID",
        initialValue = 100,
        allocationSize = 1
)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGen")
    private Long id;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    private String name;
    private String surname;

    @Schema(hidden = true)
    @ManyToOne
    @JoinColumn(name = "branch_id")
    @JsonBackReference(value = "user-reference")
    private Branch branch;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();
}
