package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main dashboard response DTO for Highcharts-compatible dashboard.
 * This structure is designed to be directly consumed by Angular Highcharts components.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private KpiDto kpis;
    private LicensesPerMonthDto licensesPerMonth;
    private CategoryDistributionDto licensesStatusPerMonth;
    private BarChartDto licensesByMunicipio;

    private LicensesPerMonthDto certificatesPerMonth;
    private BarChartDto certificatesByMunicipio;
    private CategoryDistributionDto certificatesStatusPerMonth;

    private MapChartDto empresasByMunicipio;
    private CategoryDistributionDto empresasBySociedadeComercial;
    private CategoryDistributionDto empresasByTipoEmpresa;
}
