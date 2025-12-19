package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Bar chart distribution DTO for Highcharts bar/column charts.
 * Compatible with Highcharts bar/column chart structure.
 * categories -> xAxis.categories (e.g., municipality names)
 * series -> array of series objects with name and data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarChartDto {
    private String title;
    private List<String> categories; // e.g., ["Dili", "Baucau", "Maliana"]
    private List<BarChartSeriesDto> series; // Series array for Highcharts

    /**
     * Series data for bar chart
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BarChartSeriesDto {
        private String name;      // Series name (e.g., "Licenças")
        private List<Long> data;  // Data values (e.g., [150, 80, 59])
    }
}

