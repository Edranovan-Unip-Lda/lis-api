package tl.gov.mci.lis.repositories.projection;

/**
 * Projection for counting credits grouped by city (municipio).
 * Used for distribution by municipio pie/donut chart.
 */
public interface MunicipioCountProjection {
    String getMunicipio(); // City name
    Long getTotal();
}
