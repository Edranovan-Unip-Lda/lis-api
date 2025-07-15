package tl.gov.mci.lis.models.empresa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.User;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_empresa")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Empresa extends EntityDB {
    private String nome;
    private String nif;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "utilizador")
    private User utilizador;

    private String gerente;
    private String numeroRegistoComercial;
    private String telefone;
    private String telemovel;

    @OneToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "sede")
    private Endereco sede;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "aplicante", allowSetters = true)
    private Set<Aplicante> listaAplicante;

    public Empresa() {
    }

    public Empresa(Long id, String nome, String nif) {
        this.setId(id);
        this.nome = nome;
        this.nif = nif;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "id='" + getId() + '\'' +
                ", nome='" + nome + '\'' +
                ", nif='" + nif + '\'' +
                ", gerente='" + gerente + '\'' +
                ", numeroRegistoComercial='" + numeroRegistoComercial + '\'' +
                ", telefone='" + telefone + '\'' +
                ", telemovel='" + telemovel + '\'' +
                '}';
    }
}
