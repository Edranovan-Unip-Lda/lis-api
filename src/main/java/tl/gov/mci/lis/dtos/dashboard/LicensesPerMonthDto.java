package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Credits per month grouped by type (Individual vs Company).
 * Compatible with Highcharts column chart with multiple series.
 * categories -> xAxis.categories
 * series -> array of { name, data }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesPerMonthDto {
    private Integer year;
    private List<String> categories; // Month names: ["Jan", "Feb", ...]
    private List<ChartSeriesDto> series; // [{ name: "Individual", data: [...] }, { name: "Company", data: [...] }]
}
