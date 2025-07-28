package tl.gov.mci.lis.models.dadosmestre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_dm_sociedade_comercial")
public class SociedadeComercial extends EntityDB {
    @NotNull
    private String nome;
    private String acronimo;

    @OneToMany(mappedBy = "sociedadeComercial", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "sociedadeComercial", allowSetters = true)
    private Set<Empresa> empresas;
}
