package tl.gov.mci.lis.dtos.cadastro;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro}
 */
@Value
public class PedidoInscricaoCadastroReqsDto implements Serializable {
    Long id;
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
    @Min(message = "latitude deve estar entre -90 e 90", value = -90)
    @Max(message = "latitude deve estar entre -90 e 90", value = 90)
    Double latitude;
    @Min(message = "longitude deve estar entre -180 e 180", value = -180)
    @Max(message = "longitude deve estar entre -180 e 180", value = 180)
    Double longitude;
    TipoEstabelecimento tipoEstabelecimento;
    CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;
    NivelRisco risco;
    TipoAto ato;
    ClasseAtividadeDto classeAtividade;
    String alteracoes;
    String dataEmissaoCertAnterior;
    String observacao;
    Set<DocumentoDto> documentos;
}