package tl.gov.mci.lis.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tl.gov.mci.lis.dtos.dashboard.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for dashboard DTOs to verify JSON serialization 
 * and Highcharts-compatible structure.
 */
public class DashboardDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

//    @Test
//    public void testDashboardResponseJsonStructure() throws Exception {
//        // Arrange - Create sample data
//        KpiDto kpis = new KpiDto(23L, 453L, 289L);
//
//        List<String> monthCategories = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
//                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
//
//        ChartSeriesDto individualSeries = new ChartSeriesDto("Individual",
//                Arrays.asList(10L, 15L, 20L, 25L, 30L, 35L, 40L, 45L, 50L, 55L, 60L, 65L));
//        ChartSeriesDto companySeries = new ChartSeriesDto("Company",
//                Arrays.asList(5L, 10L, 15L, 20L, 25L, 30L, 35L, 40L, 45L, 50L, 55L, 60L));
//
//        LicensesPerMonthDto licensesPerMonth = new LicensesPerMonthDto(
//                2025, monthCategories, Arrays.asList(individualSeries, companySeries));
//
//        List<PieDataPointDto> pieData = Arrays.asList(
//                new PieDataPointDto("Dili", 150L),
//                new PieDataPointDto("Baucau", 80L),
//                new PieDataPointDto("Maliana", 59L)
//        );
//        LicensesByMunicipioDto licensesByMunicipio = new LicensesByMunicipioDto(
//                "Distribuição dos Municípios", pieData);
//
//        List<String> statusCategories = Arrays.asList("January", "February", "March",
//                "April", "May", "June", "July");
//        ChartSeriesDto activeSeries = new ChartSeriesDto("Ativos",
//                Arrays.asList(100L, 110L, 120L, 130L, 140L, 150L, 160L));
//        ChartSeriesDto expiredSeries = new ChartSeriesDto("Expirados",
//                Arrays.asList(10L, 15L, 20L, 25L, 30L, 35L, 40L));
//
//        LicensesStatusPerMonthDto licensesStatusPerMonth = new LicensesStatusPerMonthDto(
//                "January - July 2025", statusCategories,
//                Arrays.asList(activeSeries, expiredSeries));
//
//        DashboardResponse response = new DashboardResponse(
//                kpis, licensesPerMonth, licensesByMunicipio, licensesStatusPerMonth);
//
//        // Act - Serialize to JSON
//        String json = objectMapper.writeValueAsString(response);
//
//        // Assert - Verify JSON structure
//        assertNotNull(json);
//        assertTrue(json.contains("\"kpis\""));
//        assertTrue(json.contains("\"applicantsInProgress\":23"));
//        assertTrue(json.contains("\"activeLicenses\":453"));
//        assertTrue(json.contains("\"registeredCompanies\":289"));
//
//        // Verify licenses per month structure
//        assertTrue(json.contains("\"licensesPerMonth\""));
//        assertTrue(json.contains("\"year\":2025"));
//        assertTrue(json.contains("\"categories\""));
//        assertTrue(json.contains("\"series\""));
//        assertTrue(json.contains("\"Individual\""));
//        assertTrue(json.contains("\"Company\""));
//
//        // Verify municipio structure
//        assertTrue(json.contains("\"licensesByMunicipio\""));
//        assertTrue(json.contains("\"Distribuição dos Municípios\""));
//        assertTrue(json.contains("\"Dili\""));
//        assertTrue(json.contains("\"y\":150"));
//
//        // Verify status per month structure
//        assertTrue(json.contains("\"licensesStatusPerMonth\""));
//        assertTrue(json.contains("\"January - July 2025\""));
//        assertTrue(json.contains("\"Ativos\""));
//        assertTrue(json.contains("\"Expirados\""));
//
//        // Deserialize back
//        DashboardResponse deserialized = objectMapper.readValue(json, DashboardResponse.class);
//        assertNotNull(deserialized);
//        assertEquals(23L, deserialized.getKpis().getAplicantesEmCurso());
//        assertEquals(2025, deserialized.getLicensesPerMonth().getYear());
//        assertEquals(12, deserialized.getLicensesPerMonth().getCategories().size());
//        assertEquals(2, deserialized.getLicensesPerMonth().getSeries().size());
//    }

    @Test
    public void testChartSeriesDtoStructure() throws Exception {
        ChartSeriesDto series = new ChartSeriesDto("Test Series", 
                Arrays.asList(1L, 2L, 3L, 4L, 5L));
        
        String json = objectMapper.writeValueAsString(series);
        
        assertTrue(json.contains("\"name\":\"Test Series\""));
        assertTrue(json.contains("\"data\":[1,2,3,4,5]"));
    }

    @Test
    public void testPieDataPointDtoStructure() throws Exception {
        PieDataPointDto dataPoint = new PieDataPointDto("Category A", 100L);
        
        String json = objectMapper.writeValueAsString(dataPoint);
        
        assertTrue(json.contains("\"name\":\"Category A\""));
        assertTrue(json.contains("\"y\":100"));
    }
}
