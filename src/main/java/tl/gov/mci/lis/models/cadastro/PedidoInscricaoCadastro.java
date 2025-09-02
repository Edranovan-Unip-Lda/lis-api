package tl.gov.mci.lis.models.cadastro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Entity
@Getter
@Setter
@Table(name = "lis_cadastro_pedido_inscricao")
public class PedidoInscricaoCadastro extends EntityDB {
    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    @Enumerated(EnumType.STRING)
    private TipoPedidoCadastro tipoPedidoCadastro;

    private String nomeEmpresa;
    private String empresaNif;
    private String empresaGerente;
    private String empresaNumeroRegistoComercial;
    private String empresaEmail;
    private String empresaTelefone;
    private String empresaTelemovel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "aplicante")
    private Endereco empresaSede;

    private String categoria;

    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;

    @Enumerated(EnumType.STRING)
    private QuantoAtividade quantoAtividade;

    private String nomeEstabelecimento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_estabelecimento_id", referencedColumnName = "id")
    private Endereco localEstabelecimento;

    @Enumerated(EnumType.STRING)
    private TipoEstabelecimento tipoEstabelecimento;

    @Enumerated(EnumType.STRING)
    private CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;

    @Enumerated(EnumType.STRING)
    private NivelRisco risco;

    @Enumerated(EnumType.STRING)
    private TipoAto ato;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_atividade_id", nullable = false)
    @JsonIgnoreProperties(value = "listaPedidoInscricaoCadastro", allowSetters = true)
    private ClasseAtividade classeAtividade;


    private String alteracoes;

    private String dataEmissaoCertAnterior;

    private String observacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "aplicante_id", referencedColumnName = "id")
    private Aplicante aplicante;

    @OneToOne(mappedBy = "pedidoInscricaoCadastro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Fatura fatura;

    @OneToOne(mappedBy = "pedidoInscricaoCadastro", cascade = CascadeType.ALL, orphanRemoval = true)
    private CertificadoInscricaoCadastro certificadoInscricaoCadastro;

    public PedidoInscricaoCadastro() {
    }

    public PedidoInscricaoCadastro(Long id, TipoPedidoCadastro tipoPedidoCadastro, String nomeEmpresa, String nif, String gerente, String numeroRegistoComercial, String email, String telefone, String telemovel, Long sedeId, String categoria, TipoEmpresa tipoEmpresa, String nomeEstabelecimento, Long localEstabelecimentoId, TipoEstabelecimento tipoEstabelecimento, CaraterizacaoEstabelecimento caraterizacaoEstabelecimento, NivelRisco risco, TipoAto ato, String alteracoes, String dataEmissaoCertAnterior, String observacao) {
        this.setId(id);
        this.tipoPedidoCadastro = tipoPedidoCadastro;
        this.nomeEmpresa = nomeEmpresa;
        this.empresaNif = nif;
        this.empresaGerente = gerente;
        this.empresaNumeroRegistoComercial = numeroRegistoComercial;
        this.empresaEmail = email;
        this.empresaTelefone = telefone;
        this.empresaTelemovel = telemovel;
        this.empresaSede = new Endereco();
        this.empresaSede.setId(sedeId);
        this.categoria = categoria;
        this.tipoEmpresa = tipoEmpresa;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.localEstabelecimento.setId(localEstabelecimentoId);
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.caraterizacaoEstabelecimento = caraterizacaoEstabelecimento;
        this.risco = risco;
        this.ato = ato;
        this.alteracoes = alteracoes;
        this.dataEmissaoCertAnterior = dataEmissaoCertAnterior;
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "PedidoInscricaoCadastro{" +
                "id=" + this.getId() +
                "tipoPedido='" + tipoPedidoCadastro + '\'' +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                ", nif='" + empresaNif + '\'' +
                ", gerente='" + empresaGerente + '\'' +
                ", numeroRegistoComercial='" + empresaNumeroRegistoComercial + '\'' +
                ", email='" + empresaEmail + '\'' +
                ", telefone='" + empresaTelefone + '\'' +
                ", telemovel='" + empresaTelemovel + '\'' +
                ", categoria='" + categoria + '\'' +
                ", tipoEmpresa='" + tipoEmpresa + '\'' +
                ", nomeEstabelecimento='" + nomeEstabelecimento + '\'' +
                ", localEstabelecimento='" + localEstabelecimento + '\'' +
                ", tipoEstabelecimento='" + tipoEstabelecimento + '\'' +
                ", caraterizacaoEstabelecimento='" + caraterizacaoEstabelecimento + '\'' +
                ", risco='" + risco + '\'' +
                ", ato='" + ato + '\'' +
                ", alteracoes='" + alteracoes + '\'' +
                ", dataEmissaoCertAnterior='" + dataEmissaoCertAnterior + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
