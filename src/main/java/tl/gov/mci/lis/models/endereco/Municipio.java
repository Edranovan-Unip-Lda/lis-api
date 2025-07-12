package tl.gov.mci.lis.models.endereco;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

import java.util.Set;

@Entity
@Table(name = "md_municipio")
@Getter
@Setter
public class Municipio extends EntityDB {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "municipio", allowSetters = true)
    private Set<PostoAdministrativo> listaPostoAdministrativo;

    public Municipio() {
    }

    public Municipio(Long id, String nome) {
        this.setId(id);
        this.setNome(nome);
    }

    @Override
    public String toString() {
        return "Municipio{" +
                "nome='" + nome + '\'' +
                '}';
    }
}
