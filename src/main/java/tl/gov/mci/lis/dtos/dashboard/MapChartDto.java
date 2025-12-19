package tl.gov.mci.lis.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Map chart distribution DTO for Highcharts map charts.
 * Compatible with Highcharts map chart structure.
 * Each data point contains municipality key and value for coloring.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapChartDto {
    private String title;
    private List<MapDataPointDto> data;

    /**
     * Individual map data point
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MapDataPointDto {
        private String name;      // Municipality name (e.g., "Dili")
        private String code;      // Municipality code for map matching (e.g., "tl-di")
        private Long value;       // Data value for coloring
    }
}

