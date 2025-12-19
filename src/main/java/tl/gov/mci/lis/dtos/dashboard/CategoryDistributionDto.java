package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic distribution DTO for categorized data.
 * Compatible with Highcharts pie/donut/map charts.
 * Can be used for any categorical distribution (municipio, type, status, etc.)
 * series -> array of { name, y }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDistributionDto {
    private String title;
    private List<PieDataPointDto> series;
}

