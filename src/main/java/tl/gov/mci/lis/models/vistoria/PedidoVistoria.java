package tl.gov.mci.lis.models.vistoria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;
import tl.gov.mci.lis.enums.vistoria.TipoVistoria;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Entity
@Table(name = "lis_atividade_pedido_vistoria")
@Getter
@Setter
public class PedidoVistoria extends EntityDB {
    @Column(nullable = false)
    private int jornada;

    @Enumerated(EnumType.STRING)
    private TipoVistoria tipoVistoria;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    private String nomeEmpresa;
    private String empresaNif;
    private String empresaGerente;
    private String empresaNumeroRegistoComercial;
    private String empresaEmail;
    private String empresaTelefone;
    private String empresaTelemovel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "empresa_sede_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "aplicante")
    private Endereco empresaSede;

    private String nomeEstabelecimento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "local_estabelecimento_id", referencedColumnName = "id")
    private Endereco localEstabelecimento;

    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;

    @Enumerated(EnumType.STRING)
    private CaraterizacaoEstabelecimento tipoEstabelecimento;

    @Enumerated(EnumType.STRING)
    private NivelRisco risco;

    @Enumerated(EnumType.STRING)
    private TipoAto atividade;

    @Enumerated(EnumType.STRING)
    private QuantoAtividade tipoAtividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_atividade_id", nullable = false)
    @JsonIgnoreProperties(value = "listaPedidoInscricaoCadastro", allowSetters = true)
    private ClasseAtividade classeAtividade;

    @Column(columnDefinition = "TEXT")
    private String alteracoes;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToOne(mappedBy = "pedidoVistoria", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private AutoVistoria autoVistoria;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_licenca_atividade_id", referencedColumnName = "id")
    private PedidoLicencaAtividade pedidoLicencaAtividade;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "fatura_id", referencedColumnName = "id")
    private Fatura fatura;

    @Override
    public String toString() {
        return "PedidoInscricaoCadastro{" +
                "id=" + this.getId() +
                "tipo='" + tipoVistoria + '\'' +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                ", localEstabelecimento='" + localEstabelecimento + '\'' +
                ", tipoEstabelecimento='" + tipoEstabelecimento + '\'' +
                ", risco='" + risco + '\'' +
                '}';
    }
}
