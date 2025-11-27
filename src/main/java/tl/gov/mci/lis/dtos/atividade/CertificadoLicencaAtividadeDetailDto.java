package tl.gov.mci.lis.dtos.atividade;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.licenca.ArrendadorDto;
import tl.gov.mci.lis.dtos.licenca.PessoaDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.enums.atividade.TipoPedidoAtividade;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade}
 */
@Value
public class CertificadoLicencaAtividadeDetailDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    PedidoLicencaAtividadeDto pedidoLicencaAtividade;
    @NotNull
    String sociedadeComercial;
    @NotNull
    String numeroRegistoComercial;
    String nif;
    EnderecoDto sede;
    @NotNull
    NivelRisco nivelRisco;
    @NotNull
    String atividade;
    @NotNull
    String atividadeCodigo;
    @NotNull
    String dataValidade;
    @NotNull
    String dataEmissao;
    @NotNull
    String nomeDiretorGeral;

    /**
     * DTO for {@link tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade}
     */
    @Value
    public static class PedidoLicencaAtividadeDto implements Serializable {
        Long id;
        TipoPedidoAtividade tipo;
        PedidoStatus status;
        String nomeEmpresa;
        String empresaNumeroRegistoComercial;
        GrupoAtividadeDto tipoAtividade;
        NivelRisco risco;
        boolean estatutoSociedadeComercial;
        String empresaNif;
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
        AplicanteDto aplicante;
        ClasseAtividadeDto classeAtividade;
        ArrendadorDto arrendador;

        /**
         * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
         */
        @Value
        public static class AplicanteDto implements Serializable {
            Long id;
            AplicanteType tipo;
            Categoria categoria;
            String numero;
            AplicanteStatus estado;
            EmpresaDto empresa;
        }
    }
}