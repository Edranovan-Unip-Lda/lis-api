package tl.gov.mci.lis.dtos.report;

import lombok.Data;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;

import java.time.Instant;

/**
 * Filter DTO for Aplicante report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class AplicanteReportFilter {
    private AplicanteType tipo;
    private Categoria categoria;
    private String numero;
    private AplicanteStatus estado;
    private Long empresaId;
    private Instant updatedAtFrom;
    private Instant updatedAtTo;
}
