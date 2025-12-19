package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * KPI metrics for dashboard header cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiDto {
    private Long aplicantesEmCurso;
    private Long licencasAtivas;
    private Long empresasRegistradas;
}
