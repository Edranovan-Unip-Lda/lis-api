package tl.gov.mci.lis.dtos.atividade;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade}
 */
@Value
public class CertificadoLicencaAtividadeDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    AplicanteDto aplicante;
    @NotNull
    String sociedadeComercial;
    String tipoSociedadeComercial;
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
    DocumentoDto assinatura;

    /**
     * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
     */
    @Value
    public static class AplicanteDto implements Serializable {
        Long id;
        Instant createdAt;
        Instant updatedAt;
        String createdBy;
        String updatedBy;
        AplicanteType tipo;
        Categoria categoria;
        String numero;
        AplicanteStatus estado;
    }
}