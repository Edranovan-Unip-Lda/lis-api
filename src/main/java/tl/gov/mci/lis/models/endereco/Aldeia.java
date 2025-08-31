package tl.gov.mci.lis.models.endereco;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

import java.util.Objects;

@Entity
@Table(name = "lis_dm_aldeia")
@Getter
@Setter
public class Aldeia extends EntityDB {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suco_id", nullable = false)
    @JsonIgnoreProperties(value = "listaAldeia", allowSetters = true)
    private Suco suco;

    public Aldeia() {
    }

    public Aldeia(Long id, String nome) {
        this.setId(id);
        this.nome = nome;
    }

    public Aldeia(Long id,String nome, Long sucoId, String sucoNome, Long postoAdministrativoId, String postoAdministrativoNome, Long municipioId, String municipioNome) {
        this.setId(id);
        this.nome = nome;
        this.suco = new Suco(sucoId, sucoNome, postoAdministrativoId, postoAdministrativoNome, municipioId, municipioNome);
    }

    @Override
    public String
    toString() {
        return "Aldeia{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
