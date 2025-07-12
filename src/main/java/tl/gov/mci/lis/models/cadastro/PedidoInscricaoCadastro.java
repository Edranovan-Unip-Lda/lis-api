package tl.gov.mci.lis.models.cadastro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Getter
@Setter
@Table(name = "lis_pedido_inscricao_cadastro")
public class PedidoInscricaoCadastro extends EntityDB {

    private String tipoPedido;

    private String nomeEmpresa;
    private String nif;
    private String gerente;
    private String numeroRegistoComercial;
    private String email;
    private String telefone;
    private String telemovel;

    @OneToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "sede")
    private Endereco sede;

    private String categoria;

    private String tipoEmpresa;

    private String nomeEstabelecimento;

    private String localEstabelecimento;

    private String tipoEstabelecimento;

    private String caraterizacaoEstabelecimento;

    private String risco;

    private String ato;

    private String tipoAtividade;

    private String tipoAtividadeCodigo;

    private String atividadePrincipal;

    private String atividadePrincipalCodigo;

    private String alteracoes;

    private String dataEmissaoCertAnterior;

    private String observacao;

    @OneToOne
    @JoinColumn(name = "aplicante_id", referencedColumnName = "id")
    private Aplicante aplicante;

    public PedidoInscricaoCadastro() {}

    public PedidoInscricaoCadastro(Long id,String tipoPedido, String nomeEmpresa, String nif, String gerente, String numeroRegistoComercial, String email, String telefone, String telemovel, Long sedeId, String categoria, String tipoEmpresa, String nomeEstabelecimento, String localEstabelecimento, String tipoEstabelecimento, String caraterizacaoEstabelecimento, String risco, String ato, String tipoAtividade, String tipoAtividadeCodigo, String atividadePrincipal, String atividadePrincipalCodigo, String alteracoes, String dataEmissaoCertAnterior, String observacao) {
        this.setId(id);
        this.tipoPedido = tipoPedido;
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
        this.tipoAtividade = tipoAtividade;
        this.tipoAtividadeCodigo = tipoAtividadeCodigo;
        this.atividadePrincipal = atividadePrincipal;
        this.atividadePrincipalCodigo = atividadePrincipalCodigo;
        this.alteracoes = alteracoes;
        this.dataEmissaoCertAnterior = dataEmissaoCertAnterior;
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "PedidoInscricaoCadastro{" +
                "id=" + this.getId() +
                "tipoPedido='" + tipoPedido + '\'' +
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
                ", tipoAtividade='" + tipoAtividade + '\'' +
                ", tipoAtividadeCodigo='" + tipoAtividadeCodigo + '\'' +
                ", atividadePrincipal='" + atividadePrincipal + '\'' +
                ", atividadePrincipalCodigo='" + atividadePrincipalCodigo + '\'' +
                ", alteracoes='" + alteracoes + '\'' +
                ", dataEmissaoCertAnterior='" + dataEmissaoCertAnterior + '\'' +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
