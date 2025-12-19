package tl.gov.mci.lis.repositories.projection;

/**
 * Projection for counting credits grouped by month and demographic type (Individual/Company).
 * Used for licenses per month chart with multiple series.
 */
public interface MonthTypeCountProjection {
    Integer getMonth();
    String getBeneficiary(); // "individual" or "company"
    Long getTotal();
    String getTipoLicenca();
}
