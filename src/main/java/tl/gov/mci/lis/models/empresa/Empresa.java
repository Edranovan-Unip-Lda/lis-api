package tl.gov.mci.lis.models.empresa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.TipoPropriedade;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.User;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_empresa")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Empresa extends EntityDB {
    private String nome;
    private String nif;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "utilizador")
    private User utilizador;

    @NotNull
    private String gerente;
    @NotNull
    private String numeroRegistoComercial;
    private String telefone;
    private String telemovel;

    @NotNull
    @DecimalMin(value = "0.0", message = "capitalSocial deve ser >= 0")
    private Double capitalSocial;

    @NotNull
    private LocalDate dataRegisto;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoPropriedade tipoPropriedade;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "empresa", allowSetters = true)
    private Set<Acionista> acionistas;

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

    public void addAcionista(Acionista a) {
        acionistas.add(a);
        a.setEmpresa(this);
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "utilizador=" + utilizador +
                ", nome='" + nome + '\'' +
                ", nif='" + nif + '\'' +
                ", gerente='" + gerente + '\'' +
                ", numeroRegistoComercial='" + numeroRegistoComercial + '\'' +
                ", telefone='" + telefone + '\'' +
                ", telemovel='" + telemovel + '\'' +
                ", capitalSocial=" + capitalSocial +
                ", dataRegisto=" + dataRegisto +
                ", tipoPropriedade=" + tipoPropriedade +
                '}';
    }
}
