package app.quantun.backend.repository.specification;

import app.quantun.backend.models.contract.request.CategoryFilterDTO;
import app.quantun.backend.models.entity.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for Category entities.
 * This class provides methods for creating specifications based on filter criteria.
 */
public class CategorySpecification {

    /**
     * Creates a specification for filtering categories based on the provided filter criteria.
     *
     * @param filter the filter criteria
     * @return a specification for filtering categories
     */
    public static Specification<Category> getCategorySpecification(CategoryFilterDTO filter) {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
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