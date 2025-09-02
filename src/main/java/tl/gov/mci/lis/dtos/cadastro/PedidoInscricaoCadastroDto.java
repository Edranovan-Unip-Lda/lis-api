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
    CertificadoInscricaoCadastroDto certificadoInscricaoCadastro;

    public PedidoInscricaoCadastroDto(Long id) {
        this.id = id;
    }
}