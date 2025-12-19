package tl.gov.mci.lis.repositories.projection;

/**
 * Projection for counting credits grouped by month.
 * Used for status per month charts (active vs expired).
 */
public interface MonthCountProjection {
    Integer getMonth();
    Long getTotal();
}
