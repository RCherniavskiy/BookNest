package book_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(columnDefinition = "varchar")
    private RoleName name;

    public enum RoleName {
        ROLE_USER,
        ROLE_ADMIN,
    }
}
