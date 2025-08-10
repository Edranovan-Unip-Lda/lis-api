package tl.gov.mci.lis.dtos.cadastro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoInscricaoCadastroDto implements Serializable {
    Long id;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    PedidoStatus status;
    TipoPedidoCadastro tipoPedidoCadastro;
    String nomeEmpresa;
    String empresaNif;
    String empresaGerente;
    String empresaNumeroRegistoComercial;
    String empresaEmail;
    String empresaTelefone;
    String empresaTelemovel;
    EnderecoDto empresaSede;
    String categoria;
    TipoEmpresa tipoEmpresa;
    QuantoAtividade quantoAtividade;
    String nomeEstabelecimento;
    EnderecoDto localEstabelecimento;
    TipoEstabelecimento tipoEstabelecimento;
    CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;
    NivelRisco risco;
    TipoAto ato;
    ClasseAtividadeDto classeAtividade;
    String alteracoes;
    String dataEmissaoCertAnterior;
    String observacao;
    FaturaDto fatura;

    public PedidoInscricaoCadastroDto(Long id) {
        this.id = id;
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
            TipoEmpresa tipoEmpresa,
            QuantoAtividade quantoAtividade,
            String nomeEstabelecimento,
            EnderecoDto localEstabelecimento,
            TipoEstabelecimento tipoEstabelecimento,
            CaraterizacaoEstabelecimento caraterizacaoEstabelecimento,
            NivelRisco risco,
            TipoAto ato,
            ClasseAtividadeDto classeAtividade,
            String alteracoes,
            String dataEmissaoCertAnterior,
            String observacao,
            FaturaDto fatura,
            String empresaGerente) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = status;
        this.tipoPedidoCadastro = tipoPedidoCadastro;
        this.nomeEmpresa = nomeEmpresa;
        this.empresaNif = nif;
        this.empresaGerente = gerente;
        this.empresaNumeroRegistoComercial = numeroRegistoComercial;
        this.empresaEmail = email;
        this.empresaTelefone = telefone;
        this.empresaTelemovel = telemovel;
        this.empresaSede = sede;
        this.categoria = categoria;
        this.tipoEmpresa = tipoEmpresa;
        this.quantoAtividade = quantoAtividade;
        this.nomeEstabelecimento = nomeEstabelecimento;
        this.localEstabelecimento = localEstabelecimento;
        this.tipoEstabelecimento = tipoEstabelecimento;
        this.caraterizacaoEstabelecimento = caraterizacaoEstabelecimento;
        this.risco = risco;
        this.ato = ato;
        this.classeAtividade = classeAtividade;
        this.alteracoes = alteracoes;
        this.dataEmissaoCertAnterior = dataEmissaoCertAnterior;
        this.observacao = observacao;
        this.fatura = fatura;
    }
}