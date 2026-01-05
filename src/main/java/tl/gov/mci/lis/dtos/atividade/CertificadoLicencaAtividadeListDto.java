package tl.gov.mci.lis.dtos.atividade;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;

/**
 * Lightweight DTO for listing CertificadoLicencaAtividade in pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoLicencaAtividadeListDto implements Serializable {
    private Long id;
    private Boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @NotNull
    private String sociedadeComercial;

    @NotNull
    private String numeroRegistoComercial;

    private String nif;

    @NotNull
    private NivelRisco nivelRisco;

    @NotNull
    private String atividade;

    @NotNull
    private String atividadeCodigo;

    @NotNull
    private String dataValidade;

    @NotNull
    private String dataEmissao;

    @NotNull
    private String nomeDiretorGeral;

    // Complete address hierarchy
    private String sedeLocal;
    private String sedeAldeiaNome;
    private String sedeSucoNome;
    private String sedePostoNome;
    private String sedeMunicipioNome;

    // Only the aplicante numero from parent
    private String aplicanteNumero;
}

