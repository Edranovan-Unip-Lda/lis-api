package tl.gov.mci.lis.models.empresa;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.TipoPropriedade;
import tl.gov.mci.lis.enums.cadastro.TipoEmpresa;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.dadosmestre.SociedadeComercial;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.User;

import java.time.LocalDate;
import java.util.List;
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
    private String numeroRegistoComercial;
    private String telefone;
    private String telemovel;
    private String email;

    @NotNull
    @DecimalMin(value = "0.0", message = "capitalSocial deve ser >= 0")
    private Double capitalSocial;

    @NotNull
    private LocalDate dataRegisto;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoPropriedade tipoPropriedade;

    private Long totalTrabalhadores;
    private Double volumeNegocioAnual;
    private Double balancoTotalAnual;

    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sociedade_comercial_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = "empresas", allowSetters = true)
    private SociedadeComercial sociedadeComercial;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "empresa", allowSetters = true)
    private Set<Acionista> acionistas;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "sede", allowSetters = true)
    private Endereco sede;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gerente_id", referencedColumnName = "id")
    private Gerente gerente;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private Representante representante;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "empresa", allowSetters = true)
    private List<Documento> documentos;

    @Min(value = -90, message = "latitude deve estar entre -90 e 90")
    @Max(value = 90, message = "latitude deve estar entre -90 e 90")
    private Double latitude;

    @Min(value = -180, message = "longitude deve estar entre -180 e 180")
    @Max(value = 180, message = "longitude deve estar entre -180 e 180")
    private Double longitude;

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

    public void addDocumento(Documento d) {
        documentos.add(d);
        d.setEmpresa(this);
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
