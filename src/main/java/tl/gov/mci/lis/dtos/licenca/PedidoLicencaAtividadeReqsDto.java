package tl.gov.mci.lis.dtos.licenca;

import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.GrupoAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.atividade.TipoPedidoAtividade;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade}
 */
@Value
public class PedidoLicencaAtividadeReqsDto implements Serializable {
    Long id;
    TipoPedidoAtividade tipo;
    PedidoStatus status;
    String nomeEmpresa;
    String empresaNumeroRegistoComercial;
    EnderecoDto empresaSede;
    GrupoAtividadeDto tipoAtividade;
    NivelRisco risco;
    boolean estatutoSociedadeComercial;
    String empresaNif;
    PessoaDto representante;
    PessoaDto gerente;
    boolean planta;
    boolean documentoPropriedade;
    boolean documentoImovel;
    boolean contratoArrendamento;
    boolean planoEmergencia;
    boolean estudoAmbiental;
    Double numEmpregosCriados;
    Double numEmpregadosCriar;
    boolean reciboPagamento;
    String outrosDocumentos;
}