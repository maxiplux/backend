package app.quantun.backend.repository;

import app.quantun.backend.models.entity.Category;
import app.quantun.backend.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for managing Product entities.
 * This interface provides methods for CRUD operations and custom queries on Product entities.
 */
@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of products containing the given name
     */
    List<Product> findByNameContaining(String name);

    /**
     * Find products containing the given name with pagination.
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a page of products containing the given name
     */
    Page<Product> findByNameContaining(String name, Pageable pageable);

    /**
     * Find products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of products priced below the given value
     */
    List<Product> findByPriceLessThan(BigDecimal price);

    /**
     * Find products priced below a given value with pagination.
     *
     * @param price the maximum price
     * @param pageable pagination information
     * @return a page of products priced below the given value
     */
    Page<Product> findByPriceLessThan(BigDecimal price, Pageable pageable);

    /**
     * Find products that are currently in stock.
     *
     * @param inStock indicates whether the product is in stock
     * @return a list of products that are in stock
     */
    List<Product> findByInStock(boolean inStock);

    /**
     * Find products that are currently in stock with pagination.
     *
     * @param inStock indicates whether the product is in stock
     * @param pageable pagination information
     * @return a page of products that are in stock
     */
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByInStock(boolean inStock, Pageable pageable);

    /**
     * Find products containing the given name with slice-based pagination.
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a slice of products containing the given name
     */
    Slice<Product> findSliceByNameContaining(String name, Pageable pageable);

    /**
     * Find products priced below a given value with slice-based pagination.
     *
     * @param price the maximum price
     * @param pageable pagination information
     * @return a slice of products priced below the given value
     */
    Slice<Product> findSliceByPriceLessThan(BigDecimal price, Pageable pageable);

    /**
     * Find products that are currently in stock with slice-based pagination.
     *
     * @param inStock indicates whether the product is in stock
     * @param pageable pagination information
     * @return a slice of products that are in stock
     */
    Slice<Product> findSliceByInStock(boolean inStock, Pageable pageable);

    /**
     * Find all products by category ID with Slice pagination.
     * This method uses JPQL to query the database directly.
     *
     * @param categoryId The ID of the category to filter by
     * @param pageable Pagination parameters
     * @return A slice of products in the specified category
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Slice<Product> findProductsByCategoryIdSliced(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Find all products by category ID with additional filtering options.
     * This method uses JPQL with multiple optional filter parameters.
     *
     * @param categoryId The ID of the category to filter by
     * @param name Optional name filter (case insensitive, partial match)
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param inStock Optional in-stock status filter
     * @param pageable Pagination parameters
     * @return A slice of products matching all criteria
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
            "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR p.inStock = :inStock)")
    Slice<Product> findProductsByCategoryWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    /**
     * Find all products by category ID with eager loading of the category.
     * This query uses JOIN FETCH to reduce the N+1 query problem.
     *
     * @param categoryId The ID of the category to filter by
     * @param pageable Pagination parameters
     * @return A slice of products in the specified category with category data loaded
     */
    @Query("SELECT p FROM Product p JOIN FETCH p.category c WHERE c.id = :categoryId")
    Slice<Product> findProductsByCategoryIdWithJoinFetch(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Find all products by category ID with additional sorting by stock status.
     * This demonstrates custom ordering in JPQL.
     *
     * @param categoryId The ID of the category to filter by
     * @param pageable Pagination parameters (additional to the built-in sorting)
     * @return A slice of products ordered by in-stock status (in-stock first)
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId ORDER BY p.inStock DESC")
    Slice<Product> findProductsByCategoryIdOrderByStockStatus(@Param("categoryId") Long categoryId, Pageable pageable);

    /**
     * Find all products by category ID with optional text search across name and description.
     * This demonstrates using OR conditions in JPQL.
     *
     * @param categoryId The ID of the category to filter by
     * @param searchTerm Optional search term to look for in name or description
     * @param pageable Pagination parameters
     * @return A slice of products matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
            "AND (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Slice<Product> findProductsByCategoryIdWithTextSearch(
            @Param("categoryId") Long categoryId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT p FROM Product p") // Adding a base query
    Slice<Product> findAllWithCategory(Specification<Product> specification, Pageable pageable);
}
