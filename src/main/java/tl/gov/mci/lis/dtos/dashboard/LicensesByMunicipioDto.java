package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Distribution of credits by city (municipio).
 * Compatible with Highcharts pie/donut chart.
 * series -> array of { name, y }
 *
 * @deprecated Use {@link CategoryDistributionDto} instead.
 * This class is kept for backward compatibility but will be removed in future versions.
 */
@Deprecated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesByMunicipioDto {
    private String title;
    private List<PieDataPointDto> series;
}
