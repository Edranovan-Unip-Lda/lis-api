package tl.gov.mci.lis.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tl.gov.mci.lis.dtos.dashboard.*;
import tl.gov.mci.lis.services.dashboard.DashboardService;

import java.time.LocalDate;

/**
 * REST Controller exposing the single endpoint:
 * GET /api/dashboard?year=2025&start=2025-01-01&end=2025-07-31
 * <p>
 * Parameters:
 * - year: affects LicensesPerMonth chart (default current year)
 * - start/end: affect LicensesStatusPerMonth chart (default current year range)
 * <p>
 * Highcharts (Angular) mapping hints:
 * - Use response.licensesPerMonth.categories -> xAxis.categories for the monthly type chart.
 * - Use response.licensesPerMonth.series -> y values for "Comercial" and "Industrial".
 * - Use response.licensesByMunicipio.series as pie data points [{ name, y }, ...].
 * - Use response.licensesStatusPerMonth.categories -> xAxis.categories for the status chart.
 * - Use response.licensesStatusPerMonth.series -> y values for "Ativos" and "Expirados".
 * <p>
 * Filters:
 * - Change year to adjust monthly type aggregation.
 * - Change start/end to adjust the status-in-range logic.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(value = "year", required = false) Integer year
    ) {
        int resolvedYear = (year != null && year > 0) ? year : LocalDate.now().getYear();

        KpiDto kpis = service.getKpis();
        LicensesPerMonthDto licensesPerMonth = service.getLicensesPerMonth(resolvedYear);
        CategoryDistributionDto licensesStatusPerMonth = service.getLicensesStatusPerMonth();
        BarChartDto licensesByMunicipio = service.getLicensesByMunicipio();

        LicensesPerMonthDto certificatesPerMonth = service.getCertificatesPerMonth(resolvedYear);
        BarChartDto certificatesByMunicipio = service.getCertificatesByMunicipio();
        CategoryDistributionDto certificatesStatusPerMonth = service.getCertificatesStatusPerMonth();

        MapChartDto empresasByMunicipio = service.getEmpresasByMunicipio();
        CategoryDistributionDto empresasBySociedadeComercial = service.getEmpresasBySociedadeComercial();
        CategoryDistributionDto empresasByTipoEmpresa = service.getEmpresasByTipoEmpresa();

        DashboardResponse payload = new DashboardResponse(
                kpis,
                licensesPerMonth,
                licensesStatusPerMonth,
                licensesByMunicipio,
                certificatesPerMonth,
                certificatesByMunicipio,
                certificatesStatusPerMonth,
                empresasByMunicipio,
                empresasBySociedadeComercial,
                empresasByTipoEmpresa
        );
        return ResponseEntity.ok(payload);
    }
}
