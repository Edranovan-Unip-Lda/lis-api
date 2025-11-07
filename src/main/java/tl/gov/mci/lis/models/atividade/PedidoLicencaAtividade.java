package tl.gov.mci.lis.models.atividade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.atividade.TipoPedidoAtividade;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_atividade_pedido_licenca")
public class PedidoLicencaAtividade extends EntityDB {

    @Enumerated(EnumType.STRING)
    private TipoPedidoAtividade tipo;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private String nomeEmpresa;
    private String empresaNumeroRegistoComercial;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "aplicante")
    private Endereco empresaSede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_atividade_id", nullable = false)
    @JsonIgnoreProperties(value = "pedidoLicencaAtividadeList", allowSetters = true)
    private ClasseAtividade classeAtividade;

    @Enumerated(EnumType.STRING)
    private NivelRisco risco;
    private boolean estatutoSociedadeComercial;
    private String empresaNif;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "representante_id", referencedColumnName = "id")
    private Pessoa representante;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Arrendador arrendador;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gerente_id", referencedColumnName = "id")
    private Pessoa gerente;

    private boolean planta;
    private boolean documentoPropriedade;
    private boolean documentoImovel;
    private boolean contratoArrendamento;
    private boolean planoEmergencia;
    private boolean estudoAmbiental;
    private Double numEmpregosCriados;
    private Double numEmpregadosCriar;
    private boolean reciboPagamento;
    private String outrosDocumentos;

    @OneToMany(mappedBy = "pedidoLicencaAtividade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("pedidoLicencaAtividade")
    private Set<Documento> documentos;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "aplicante_id", referencedColumnName = "id", nullable = false)
    private Aplicante aplicante;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fatura_id", referencedColumnName = "id")
    private Fatura fatura;

    @OneToMany(mappedBy = "pedidoLicencaAtividade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("pedidoLicencaAtividade")
    private Set<PedidoVistoria> listaPedidoVistoria;

    @OneToOne(mappedBy = "pedidoLicencaAtividade")
    private CertificadoLicencaAtividade certificadoLicencaAtividade;

    @Override
    public String toString() {
        return "PedidoLicencaAtividade{" +
                "tipo=" + tipo +
                ", status=" + status +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                '}';
    }
}
