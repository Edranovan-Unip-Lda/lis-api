package tl.gov.mci.lis.dtos.report;

import lombok.Data;

import java.time.Instant;

/**
 * Filter DTO for CertificadoInscricaoCadastro report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class CertificadoInscricaoCadastroReportFilter {
    private String sociedadeComercial;
    private String numeroRegistoComercial;
    private String atividade;
    private String dataValidade;
    private String dataEmissao;
    private String nomeDiretorGeral;
    private Long aplicanteId;
    private Instant createdAtFrom;
    private Instant createdAtTo;
}
