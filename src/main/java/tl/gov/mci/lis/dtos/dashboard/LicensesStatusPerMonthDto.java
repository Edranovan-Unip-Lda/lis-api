package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Credits status over time (Active vs Expired).
 * Compatible with Highcharts line/area chart with multiple series.
 * categories -> xAxis.categories
 * series -> array of { name, data }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicensesStatusPerMonthDto {
    private String period; // e.g., "January - July 2025"
    private List<String> categories; // Full month names: ["January", "February", ...]
    private List<ChartSeriesDto> series; // [{ name: "Ativos", data: [...] }, { name: "Expirados", data: [...] }]
}
