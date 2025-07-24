package tl.gov.mci.lis.dtos.cadastro;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro}
 */
@Value
public class PedidoInscricaoCadastroDto implements Serializable {
    Long id;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    PedidoStatus status;
    TipoPedidoCadastro tipoPedidoCadastro;
    String nomeEmpresa;
    String nif;
    String gerente;
    String numeroRegistoComercial;
    String email;
    String telefone;
    String telemovel;
    EnderecoDto sede;
    String categoria;
    String tipoEmpresa;
    String nomeEstabelecimento;
    String localEstabelecimento;
    TipoEstabelecimento tipoEstabelecimento;
    CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;
    NivelRisco risco;
    TipoAto ato;
    AtividadeEconomicaDto tipoAtividade;
    AtividadeEconomicaDto atividadePrincipal;
    String alteracoes;
    String dataEmissaoCertAnterior;
    String observacao;
    FaturaDto fatura;

    /**
     * DTO for {@link tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica}
     */
    @Value
    public static class AtividadeEconomicaDto implements Serializable {
        Long id;
        String codigo;
        String descricao;
        Categoria tipo;
    }

    public PedidoInscricaoCadastroDto(
            Long id,
            Boolean isDeleted,
            Instant createdAt,
            Instant updatedAt,
            String createdBy,
            String updatedBy,
            PedidoStatus status,
            TipoPedidoCadastro tipoPedidoCadastro,
            String nomeEmpresa,
            String nif,
            String gerente,
            String numeroRegistoComercial,
            String email,
            String telefone,
            String telemovel,
            EnderecoDto sede,
            String categoria,
            String tipoEmpresa,
            String nomeEstabelecimento,
            String localEstabelecimento,
            TipoEstabelecimento tipoEstabelecimento,
            CaraterizacaoEstabelecimento caraterizacaoEstabelecimento,
            NivelRisco risco,
            TipoAto ato,
            AtividadeEconomicaDto tipoAtividade,
            AtividadeEconomicaDto atividadePrincipal,
            String alteracoes,
            String dataEmissaoCertAnterior,
            String observacao,
            FaturaDto fatura) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = status;
        this.tipoPedidoCadastro = tipoPedidoCadastro;
        this.nomeEmpresa = nomeEmpresa;
        this.nif = nif;
        this.gerente = gerente;
        this.numeroRegistoComercial = numeroRegistoComercial;
        this.email = email;
        this.telefone = telefone;
        this.telemovel = telemovel;
        this.sede = sede;
        this.categoria = categoria;
        this.tipoEmpresa = tipoEmpresa;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.localEstabelecimento = localEstabelecimento;
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.caraterizacaoEstabelecimento = caraterizacaoEstabelecimento;
        this.risco = risco;
        this.ato = ato;
        this.tipoAtividade = tipoAtividade;
        this.atividadePrincipal = atividadePrincipal;
        this.alteracoes = alteracoes;
        this.dataEmissaoCertAnterior = dataEmissaoCertAnterior;
        this.observacao = observacao;
        this.fatura = fatura;
    }
}