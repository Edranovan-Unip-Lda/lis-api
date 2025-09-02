package tl.gov.mci.lis.models.endereco;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.vistoria.Requerente;

import java.util.Set;

@Entity
@Table(name = "lis_dm_postoadministrativo")
@Getter
@Setter
public class PostoAdministrativo extends EntityDB {

    @NotBlank(message = "é obrigatório")
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false)
    @JsonIgnoreProperties(value = "listaPostoAdministrativo", allowSetters = true)
    private Municipio municipio;

    @JsonIgnoreProperties(value = "postoAdministrativo", allowSetters = true)
    @OneToMany(mappedBy = "postoAdministrativo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Suco> listaSuco;

    @OneToMany(mappedBy = "postoAdministrativo")
    @JsonIgnoreProperties(value = "postoAdministrativo", allowSetters = true)
    private Set<Requerente> listaRequerente;

    public PostoAdministrativo() {
    }

    public PostoAdministrativo(Long id, String nome) {
        this.setId(id);
        this.nome = nome;
    }

    public PostoAdministrativo(Long id, String nome, Long municipioId, String municipioNome) {
        this.setId(id);
        this.nome = nome;
        this.municipio = new Municipio(municipioId, municipioNome);
    }

    @Override
    public String toString() {
        return "PostoAdministrativo{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
