package tl.gov.mci.lis.repositories.projection;

/**
 * Generic projection for counting items grouped by a category.
 * Used for distribution by type/category pie/donut charts.
 */
public interface CategoryCountProjection {
    String getCategory(); // Category name
    Long getTotal();      // Count for this category
}

