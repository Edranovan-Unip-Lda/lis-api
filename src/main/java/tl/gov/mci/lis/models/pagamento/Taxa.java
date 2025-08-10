package tl.gov.mci.lis.models.pagamento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.EntityDB;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_taxa")
public class Taxa extends EntityDB {

    @NotNull
    private String ato;

    @NotNull
    private Double montanteMinimo;

    @NotNull
    private Double montanteMaximo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AplicanteType tipo;

    @ManyToMany(mappedBy = "taxas")
    private Set<Fatura> faturas = new HashSet<>();

    public Taxa() {
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Taxa t)) return false;
        return getId() != null && getId().equals(t.getId());
    }
    @Override public int hashCode() { return getId() != null ? getId().hashCode() : 0; }

    @Override
    public String toString() {
        return "Taxa{" +
                "id='" + getId() + '\'' +
                ", ato='" + ato + '\'' +
                ", montanteMinimo=" + montanteMinimo +
                ", montanteMaximo=" + montanteMaximo +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
