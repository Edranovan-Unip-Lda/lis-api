package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single data point for Highcharts pie/donut charts.
 * Maps to Highcharts series[0].data[i] = { name: string, y: number }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieDataPointDto {
    private String name;
    private Long y;
}
