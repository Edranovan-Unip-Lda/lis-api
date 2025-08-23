package tl.gov.mci.lis.dtos.vistoria;

import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.cadastro.*;
import tl.gov.mci.lis.enums.vistoria.TipoVistoria;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.vistoria.PedidoVistoria}
 */
@Value
public class PedidoVistoriaDto implements Serializable {
    Long id;
    TipoVistoria tipoVistoria;
    PedidoStatus status;
    String nomeEmpresa;
    String empresaNif;
    String empresaGerente;
    String empresaNumeroRegistoComercial;
    String empresaEmail;
    String empresaTelefone;
    String empresaTelemovel;
    EnderecoDto empresaSede;
    String nomeEstabelecimento;
    EnderecoDto localEstabelecimento;
    TipoEmpresa tipoEmpresa;
    CaraterizacaoEstabelecimento tipoEstabelecimento;
    NivelRisco risco;
    TipoAto atividade;
    QuantoAtividade tipoAtividade;
    ClasseAtividadeDto classeAtividade;
    String alteracoes;
    String observacao;
    FaturaDto fatura;
}