package tl.gov.mci.lis.models.endereco;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

import java.util.Set;

@Entity
@Table(name = "md_suco")
@Getter
@Setter
public class Suco extends EntityDB {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @JsonIgnoreProperties(value = "listaSuco", allowSetters = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posto_administrativo_id", nullable = false)
    private PostoAdministrativo postoAdministrativo;

    @JsonIgnoreProperties(value = "suco", allowSetters = true)
    @OneToMany(mappedBy = "suco", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Aldeia> listaAldeia;

    public Suco() {
    }

    public Suco(Long id, String nome) {
        this.setId(id);
        this.nome = nome;
    }

    public Suco(Long id, String nome, Long postoAdministrativoId, String postoAdministrativoNome, Long municipioId, String municipioNome) {
        this.nome = nome;
        this.postoAdministrativo = new PostoAdministrativo(postoAdministrativoId, postoAdministrativoNome, municipioId, municipioNome);
    }

    @Override
    public String toString() {
        return "Suco{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
