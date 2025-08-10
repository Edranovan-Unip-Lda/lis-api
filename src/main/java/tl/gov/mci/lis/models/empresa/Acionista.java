package tl.gov.mci.lis.models.empresa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "lis_empresa_acionistas")
@Getter
@Setter
public class Acionista extends EntityDB {
    @NotNull
    private String nome;
    @NotNull
    private String nif;
    @NotNull
    private String tipoDocumento;
    @NotNull
    private String numeroDocumento;
    @NotNull
    private String email;
    @NotNull
    private Double acoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = "acionistas", allowSetters = true)
    private Empresa empresa;
}
