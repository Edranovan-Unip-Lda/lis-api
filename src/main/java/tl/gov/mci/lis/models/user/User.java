package tl.gov.mci.lis.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.dadosmestre.Role;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.time.Instant;

@Entity
@Table(name = "lis_user")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends EntityDB {
    @NotBlank(message = "Firstname is mandatory")
    private String firstName;
    @NotBlank(message = "Lastname is mandatory")
    private String lastName;

    @Column(unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(unique = true)
    @NotBlank(message = "Email is mandatory")
    private String email;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnoreProperties("users")
    @ToString.Exclude
    private Role role;
    private String jwtSession;
    private String status;

    @OneToOne(mappedBy = "utilizador")
    @JsonIgnoreProperties({"listaAplicante", "utilizador", "hibernateLazyInitializer"})
    private Empresa empresa;

    @Transient
    private String oneTimePassword;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String username, String email, String password, String jwtSession, String status) {
        this.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.jwtSession = jwtSession;
        this.status = status;
    }

    public User(Long id, String firstName, String lastName, String username, String email, String password, Long roleId, String roleName, String jwtSession, String status) {
        this.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.setRole(new Role(roleId, roleName));
        this.jwtSession = jwtSession;
        this.status = status;
    }

    public User(Long id, String firstName, String lastName, String username, String email, Long roleId, String roleName, String jwtSession, String status, Instant createdDate, String createdBy) {
        this.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.setRole(new Role(roleId, roleName));
        this.jwtSession = jwtSession;
        this.status = status;
        this.setCreatedAt(createdDate);
        this.setCreatedBy(createdBy);
    }

    public User(Long id, String firstName, String lastName, String username, String email) {
        this.setId(id);
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    public boolean isAdmin() {
        return getRole().getName().equals("ROLE_ADMIN");
    }

    public boolean isStaff() {
        return getRole().getName().equals("ROLE_STAFF");
    }
}
