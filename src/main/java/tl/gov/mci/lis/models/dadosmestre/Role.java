package tl.gov.mci.lis.models.dadosmestre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.user.User;

import java.util.Set;

@Entity
@Table(name = "lis_dm_role")
@Getter
@Setter
public class Role extends EntityDB {
    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnoreProperties(value = "role", allowSetters = true)
    private Set<User> users;

    public Role() {}

    public Role(Long id, String name) {
        this.setId(id);
        this.name = name;
    }
}
