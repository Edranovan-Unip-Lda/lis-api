package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a single series for Highcharts line/column charts.
 * Maps to Highcharts series[i] = { name: string, data: number[] }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartSeriesDto {
    private String name;
    private List<Long> data;
}
