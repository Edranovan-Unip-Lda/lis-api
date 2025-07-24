package tl.gov.mci.lis.models.pagamento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_taxa")
public class Taxa extends EntityDB {

    private String ato;
    private double montante;
    private String categoria;

    @OneToMany(mappedBy = "taxa")
    @JsonIgnoreProperties(value = "taxa", allowSetters = true)
    private Set<Fatura> fatura;

    @Override
    public String toString() {
        return "Taxa{" +
                "id='" + getId() + '\'' +
                ", ato='" + ato + '\'' +
                ", montante=" + montante +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
