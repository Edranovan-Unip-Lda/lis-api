package tl.gov.mci.lis.dtos.report;

import lombok.Data;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.time.Instant;

/**
 * Filter DTO for CertificadoLicencaAtividade report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class CertificadoLicencaAtividadeReportFilter {
    private String sociedadeComercial;
    private String numeroRegistoComercial;
    private String nif;
    private NivelRisco nivelRisco;
    private String atividade;
    private String atividadeCodigo;
    private String dataValidade;
    private String dataEmissao;
    private String nomeDiretorGeral;
    private Long aplicanteId;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
