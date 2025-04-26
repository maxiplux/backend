package app.quantun.backend.repository;

import app.quantun.backend.models.entity.Category;
import app.quantun.backend.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing Category entities.
 * This interface provides methods for CRUD operations and custom queries on Category entities.
 */
@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Long>, JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    /**
     * Find categories containing the given name.
     *
     * @param name the name to search for
     * @return a list of categories containing the given name
     */
    List<Category> findByNameContaining(String name);

    /**
     * Find categories containing the given name with pagination.
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a page of categories containing the given name
     */
    Page<Category> findByNameContaining(String name, Pageable pageable);

    /**
     * Find categories containing the given name with slice-based pagination.
     *
     * @param name the name to search for
     * @param pageable pagination information
     * @return a slice of categories containing the given name
     */
    Slice<Category> findSliceByNameContaining(String name, Pageable pageable);
}
