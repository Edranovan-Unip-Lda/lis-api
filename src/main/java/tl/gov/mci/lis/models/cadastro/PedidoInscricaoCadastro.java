package tl.gov.mci.lis.models.cadastro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Entity
@Getter
@Setter
@Table(name = "lis_pedido_inscricao_cadastro")
public class PedidoInscricaoCadastro extends EntityDB {
    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    @Enumerated(EnumType.STRING)
    private TipoPedidoCadastro tipoPedidoCadastro;

    private String nomeEmpresa;
    private String nif;
    private String gerente;
    private String numeroRegistoComercial;
    private String email;
    private String telefone;
    private String telemovel;

    @OneToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "aplicante")
    private Endereco sede;

    private String categoria;

    @Enumerated(EnumType.STRING)
    private TipoEmpresa tipoEmpresa;

    private String nomeEstabelecimento;

    private String localEstabelecimento;

    @Enumerated(EnumType.STRING)
    private TipoEstabelecimento tipoEstabelecimento;

    @Enumerated(EnumType.STRING)
    private CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;

    @Enumerated(EnumType.STRING)
    private NivelRisco risco;

    @Enumerated(EnumType.STRING)
    private TipoAto ato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_atividade_id", nullable = false)
    @JsonIgnoreProperties(value = "listaPedidoInscricaoCadastro", allowSetters = true)
    private AtividadeEconomica tipoAtividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_principal_id", nullable = false)
    @JsonIgnoreProperties(value = "listaPedidoInscricaoCadastroAtividadePrincipal", allowSetters = true)
    private AtividadeEconomica atividadePrincipal;


    private String alteracoes;

    private String dataEmissaoCertAnterior;

    private String observacao;

    @OneToOne
    @JoinColumn(name = "aplicante_id", referencedColumnName = "id")
    private Aplicante aplicante;

    @OneToOne(mappedBy = "pedidoInscricaoCadastro")
    private Fatura fatura;

    public PedidoInscricaoCadastro() {
    }

    public PedidoInscricaoCadastro(Long id, TipoPedidoCadastro tipoPedidoCadastro, String nomeEmpresa, String nif, String gerente, String numeroRegistoComercial, String email, String telefone, String telemovel, Long sedeId, String categoria, TipoEmpresa tipoEmpresa, String nomeEstabelecimento, String localEstabelecimento, TipoEstabelecimento tipoEstabelecimento, CaraterizacaoEstabelecimento caraterizacaoEstabelecimento, NivelRisco risco, TipoAto ato, String alteracoes, String dataEmissaoCertAnterior, String observacao) {
        this.setId(id);
        this.tipoPedidoCadastro = tipoPedidoCadastro;
        this.nomeEmpresa = nomeEmpresa;
        this.nif = nif;
        this.gerente = gerente;
        this.numeroRegistoComercial = numeroRegistoComercial;
        this.email = email;
        this.telefone = telefone;
        this.telemovel = telemovel;
        this.sede = new Endereco();
        this.sede.setId(sedeId);
        this.categoria = categoria;
        this.tipoEmpresa = tipoEmpresa;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.localEstabelecimento = localEstabelecimento;
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
                ", nif='" + nif + '\'' +
                ", gerente='" + gerente + '\'' +
                ", numeroRegistoComercial='" + numeroRegistoComercial + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", telemovel='" + telemovel + '\'' +
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
