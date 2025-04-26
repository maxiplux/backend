package app.quantun.backend.repository.specification;

import app.quantun.backend.models.contract.request.ProductFilterDTO;
import app.quantun.backend.models.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for Product entities.
 * This class provides methods for creating specifications based on filter criteria.
 */
public class ProductSpecification {

    /**
     * Creates a specification for filtering products based on the provided filter criteria.
     *
     * @param filter the filter criteria
     * @return a specification for filtering products
     */
    public static Specification<Product> getProductSpecification(ProductFilterDTO filter) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by name
            if (StringUtils.hasText(filter.getName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + filter.getName().toLowerCase() + "%"
                ));
            }

            // Filter by description
            if (StringUtils.hasText(filter.getDescription())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + filter.getDescription().toLowerCase() + "%"
                ));
            }

            // Filter by category
            if (filter.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("category").get("id"),
                    filter.getCategoryId()
                ));
            }

            // Filter by price range
            if (filter.getMinPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("price"),
                    filter.getMinPrice()
                ));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("price"),
                    filter.getMaxPrice()
                ));
            }

            // Filter by stock status
            if (filter.getInStock() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("inStock"),
                    filter.getInStock()
                ));
            }

            // Filter by minimum stock
            if (filter.getMinStock() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("stock"),
                    filter.getMinStock()
                ));
            }

            // Filter by creation date range
            if (filter.getCreatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"),
                    filter.getCreatedAfter()
                ));
            }

            if (filter.getCreatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"),
                    filter.getCreatedBefore()
                ));
            }

            // Filter by update date range
            if (filter.getUpdatedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("updatedAt"),
                    filter.getUpdatedAfter()
                ));
            }

            if (filter.getUpdatedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("updatedAt"),
                    filter.getUpdatedBefore()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}