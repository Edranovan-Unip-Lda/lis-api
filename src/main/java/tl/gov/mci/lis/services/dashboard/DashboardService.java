package tl.gov.mci.lis.services.dashboard;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tl.gov.mci.lis.dtos.dashboard.*;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.atividade.CertificadoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.projection.CategoryCountProjection;
import tl.gov.mci.lis.repositories.projection.MonthTypeCountProjection;
import tl.gov.mci.lis.repositories.projection.MunicipioCountProjection;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DashboardService aggregates data for the Angular Highcharts dashboard.
 * <p>
 * Highcharts mapping reminders:
 * - categories: xAxis categories (months labels, etc.)
 * - series: array of { name, data } for column/line, or array of { name, y } for pie
 * <p>
 * Filters:
 * - year: used by getLicensesPerMonth(year)
 * - start/end: used by getLicensesStatusPerMonth(start, end)
 */
@Service
@RequiredArgsConstructor
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    private static final Locale LOCALE_EN = Locale.ENGLISH; // Use Locale("pt", "PT") if you want Portuguese labels.
    private static final String STATUS_EXPIRED = "EXPIRED";

    private final AplicanteRepository aplicanteRepository;
    private final EmpresaRepository empresaRepository;
    private final CertificadoLicencaAtividadeRepository licencaRepository;
    private final CertificadoInscricaoCadastroRepository cadastroRepository;

    public KpiDto getKpis() {
        logger.info("getKpis");

        long aplicantesEmCurso;
        try {
            aplicantesEmCurso = aplicanteRepository.countByEstado(AplicanteStatus.EM_CURSO);
        } catch (Exception e) {
            // Fallback query if your domain uses enums or different values
            aplicantesEmCurso = aplicanteRepository.countInProgressFallback();
        }

        long licencasAtivas = licencaRepository.countActiveByDataValidade(LocalDate.now().toString());
        long certificadosAtivos = cadastroRepository.countActiveByDataValidade(LocalDate.now().toString());

        // Get by user status active
        long empresasRegistradas = empresaRepository.countByIsDeletedFalse();

        return new KpiDto(aplicantesEmCurso, licencasAtivas + certificadosAtivos, empresasRegistradas);
    }

    public LicensesPerMonthDto getLicensesPerMonth(int year) {
        logger.info("getLicensesPerMonth: {}", year);

        List<MonthTypeCountProjection> grouped = licencaRepository.countByMonthAndTipoLicenca(year);
        // Prepare zero-filled series for 12 months
        List<Long> comercial = IntStream.rangeClosed(1, 12).mapToObj(i -> 0L).collect(Collectors.toList());
        List<Long> industrial = IntStream.rangeClosed(1, 12).mapToObj(i -> 0L).collect(Collectors.toList());

        for (MonthTypeCountProjection row : grouped) {
            int monthIndex = Optional.ofNullable(row.getMonth()).orElse(0) - 1; // 0-based
            if (monthIndex >= 0 && monthIndex < 12) {
                switch (row.getTipoLicenca()) {
                    case "COMERCIAL" -> comercial.set(monthIndex, Optional.ofNullable(row.getTotal()).orElse(0L));
                    case "INDUSTRIAL" -> industrial.set(monthIndex, Optional.ofNullable(row.getTotal()).orElse(0L));
                }
            }
        }

        List<String> categories = monthShortNames();
        List<ChartSeriesDto> series = new ArrayList<>();
        series.add(new ChartSeriesDto("Comercial", comercial));
        series.add(new ChartSeriesDto("Industrial", industrial));

        return new LicensesPerMonthDto(year, categories, series);
    }

    public BarChartDto getLicensesByMunicipio() {
        logger.info("getLicensesByMunicipio");
        List<MunicipioCountProjection> rows = licencaRepository.countActiveByMunicipio(LocalDate.now().toString());
        return buildBarChartFromMunicipio(rows, "Licenças por Município");
    }

    public CategoryDistributionDto getLicensesStatusPerMonth() {
        logger.info("getLicensesStatus");

        String today = LocalDate.now().toString();

        long ativos = licencaRepository.countActiveByDataValidade(today);
        long expirados = licencaRepository.countExpiredByDataValidade(today);

        List<PieDataPointDto> points = new ArrayList<>();
        points.add(new PieDataPointDto("Ativos", ativos));
        points.add(new PieDataPointDto("Expirados", expirados));

        return new CategoryDistributionDto("Status das Licenças", points);
    }


    public LicensesPerMonthDto getCertificatesPerMonth(int year) {
        logger.info("getCertificatesPerMonth: {}", year);

        List<MonthTypeCountProjection> grouped = cadastroRepository.countByMonthAndTipoLicenca(year);
        // Prepare zero-filled series for 12 months
        List<Long> comercial = IntStream.rangeClosed(1, 12).mapToObj(i -> 0L).collect(Collectors.toList());
        List<Long> industrial = IntStream.rangeClosed(1, 12).mapToObj(i -> 0L).collect(Collectors.toList());

        for (MonthTypeCountProjection row : grouped) {
            int monthIndex = Optional.ofNullable(row.getMonth()).orElse(0) - 1; // 0-based
            if (monthIndex >= 0 && monthIndex < 12) {
                switch (row.getTipoLicenca()) {
                    case "COMERCIAL" -> comercial.set(monthIndex, Optional.ofNullable(row.getTotal()).orElse(0L));
                    case "INDUSTRIAL" -> industrial.set(monthIndex, Optional.ofNullable(row.getTotal()).orElse(0L));
                }
            }
        }

        List<String> categories = monthShortNames();
        List<ChartSeriesDto> series = new ArrayList<>();
        series.add(new ChartSeriesDto("Comercial", comercial));
        series.add(new ChartSeriesDto("Industrial", industrial));

        return new LicensesPerMonthDto(year, categories, series);
    }

    public BarChartDto getCertificatesByMunicipio() {
        logger.info("getCertificatesByMunicipio");
        List<MunicipioCountProjection> rows = cadastroRepository.countActiveByMunicipio(LocalDate.now().toString());
        return buildBarChartFromMunicipio(rows, "Certificados por Município");
    }

    public CategoryDistributionDto getCertificatesStatusPerMonth() {
        logger.info("getCertificatesStatus");

        String today = LocalDate.now().toString();

        long ativos = cadastroRepository.countActiveByDataValidade(today);
        long expirados = cadastroRepository.countExpiredByDataValidade(today);

        List<PieDataPointDto> points = new ArrayList<>();
        points.add(new PieDataPointDto("Ativos", ativos));
        points.add(new PieDataPointDto("Expirados", expirados));

        return new CategoryDistributionDto("Status dos Certificados", points);
    }

    public MapChartDto getEmpresasByMunicipio() {
        logger.info("getEmpresasByMunicipio");
        List<MunicipioCountProjection> rows = empresaRepository.countByMunicipio();
        return buildMapChartFromMunicipio(rows, "Empresas por Município");
    }

    public CategoryDistributionDto getEmpresasBySociedadeComercial() {
        logger.info("getEmpresasBySociedadeComercial");
        List<CategoryCountProjection> rows = empresaRepository.countBySociedadeComercial();
        return buildCategoryDistributionFromCategory(rows, "Distribuição de Empresas por Sociedade Comercial");
    }

    public CategoryDistributionDto getEmpresasByTipoEmpresa() {
        logger.info("getEmpresasByTipoEmpresa");
        List<CategoryCountProjection> rows = empresaRepository.countByTipoEmpresa();
        return buildCategoryDistributionFromCategory(rows, "Distribuição de Empresas por Tipo");
    }

    // Helpers

    /**
     * Build BarChartDto from MunicipioCountProjection list.
     * Suitable for Highcharts bar/column charts.
     * Always returns all Timor-Leste municipalities in fixed order with 0 for missing data.
     */
    private BarChartDto buildBarChartFromMunicipio(
            List<MunicipioCountProjection> rows, String title) {
        // Fixed list of all Timor-Leste municipalities in alphabetical order
        List<String> allMunicipalities = List.of(
            "Aileu", "Ainaro", "Atauro", "Baucau", "Bobonaro",
            "Cova Lima", "Dili", "Ermera", "Lautem", "Liquiça",
            "Manatuto", "Manufahi", "Oecusse", "Viqueque"
        );

        // Create a map from database results for quick lookup
        Map<String, Long> dataMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(rows)) {
            for (MunicipioCountProjection r : rows) {
                String municipio = Optional.ofNullable(r.getMunicipio()).orElse("");
                Long total = Optional.ofNullable(r.getTotal()).orElse(0L);
                dataMap.put(municipio, total);
            }
        }

        // Build data array with 0 for municipalities without data
        List<Long> data = new ArrayList<>();
        for (String municipality : allMunicipalities) {
            data.add(dataMap.getOrDefault(municipality, 0L));
        }

        // Create series with proper Highcharts structure
        BarChartDto.BarChartSeriesDto series = new BarChartDto.BarChartSeriesDto(
            title, // Series name
            data   // Series data
        );

        return new BarChartDto(title, allMunicipalities, List.of(series));
    }

    /**
     * Build MapChartDto from MunicipioCountProjection list.
     * Suitable for Highcharts map visualization.
     * Returns all 14 municipalities with codes for map matching.
     */
    private MapChartDto buildMapChartFromMunicipio(
            List<MunicipioCountProjection> rows, String title) {
        // Fixed list of all Timor-Leste municipalities with their map codes
        Map<String, String> municipalityCodes = Map.ofEntries(
            Map.entry("Aileu", "tl-al"),
            Map.entry("Ainaro", "tl-an"),
            Map.entry("Atauro", "tl-at"),
            Map.entry("Baucau", "tl-bc"),
            Map.entry("Bobonaro", "tl-bb"),
            Map.entry("Cova Lima", "tl-cl"),
            Map.entry("Dili", "tl-dl"),
            Map.entry("Ermera", "tl-er"),
            Map.entry("Lautem", "tl-la"),
            Map.entry("Liquiça", "tl-lq"),
            Map.entry("Manatuto", "tl-mt"),
            Map.entry("Manufahi", "tl-mf"),
            Map.entry("Oecusse", "tl-oe"),
            Map.entry("Viqueque", "tl-vq")
        );

        // Create a map from database results for quick lookup
        Map<String, Long> dataMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(rows)) {
            for (MunicipioCountProjection r : rows) {
                String municipio = Optional.ofNullable(r.getMunicipio()).orElse("");
                Long total = Optional.ofNullable(r.getTotal()).orElse(0L);
                dataMap.put(municipio, total);
            }
        }

        // Build map data points with all municipalities
        List<MapChartDto.MapDataPointDto> mapData = new ArrayList<>();
        for (Map.Entry<String, String> entry : municipalityCodes.entrySet()) {
            String municipality = entry.getKey();
            String code = entry.getValue();
            Long value = dataMap.getOrDefault(municipality, 0L);

            mapData.add(new MapChartDto.MapDataPointDto(municipality, code, value));
        }

        return new MapChartDto(title, mapData);
    }

    /**
     * Build CategoryDistributionDto from CategoryCountProjection list.
     * Reusable helper to eliminate code duplication.
     */
    private CategoryDistributionDto buildCategoryDistributionFromCategory(
            List<CategoryCountProjection> rows, String title) {
        List<PieDataPointDto> points = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rows)) {
            for (CategoryCountProjection r : rows) {
                String category = Optional.ofNullable(r.getCategory()).orElse("Desconhecido");
                points.add(new PieDataPointDto(category, Optional.ofNullable(r.getTotal()).orElse(0L)));
            }
        }
        return new CategoryDistributionDto(title, points);
    }

    private static List<String> monthShortNames() {
        return IntStream.rangeClosed(1, 12)
                .mapToObj(m -> java.time.Month.of(m).getDisplayName(TextStyle.SHORT, LOCALE_EN))
                .collect(Collectors.toList());
    }
}
