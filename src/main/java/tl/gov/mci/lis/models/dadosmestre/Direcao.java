package tl.gov.mci.lis.models.dadosmestre;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.user.User;

import java.util.Set;

@Entity
@Table(name = "lis_dm_direcao")
@Getter
@Setter
public class Direcao extends EntityDB {
    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private Categoria nome;

    private String codigo;

    @OneToMany(mappedBy = "direcao", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "direcao", allowSetters = true)
    private Set<User> users;

    @OneToMany(mappedBy = "direcaoAtribuida", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "direcaoAtribuida", allowSetters = true)
    private Set<Aplicante> aplicantesAtribuidos;

    public Direcao() {
    }

    public Direcao(Long id, Categoria nome) {
        this.setId(id);
        this.nome = nome;
    }

    public void addAplicante(Aplicante aplicante) {
        aplicantesAtribuidos.add(aplicante);
        aplicante.setDirecaoAtribuida(this);
    }

    public void removeAplicante(Aplicante aplicante) {
        aplicantesAtribuidos.remove(aplicante);
        aplicante.setDirecaoAtribuida(null);
    }
}
