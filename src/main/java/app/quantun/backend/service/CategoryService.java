package app.quantun.backend.service;

import app.quantun.backend.models.contract.request.CategoryFilterDTO;
import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.models.contract.response.CategoryResponseDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<CategoryResponseDTO> getAllCategories();

    Page<CategoryResponseDTO> getAllCategoriesPaged(Pageable pageable);

    Optional<CategoryResponseDTO> getCategoryById(Long id);

    @Transactional
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);

    @Transactional
    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO);

    void updateCategoryFields(Category existingCategory, CategoryRequestDTO categoryRequestDTO);

    @Transactional
    void deleteCategory(Long id);

    List<CategoryResponseDTO> searchCategoriesByName(String name);

    Page<CategoryResponseDTO> searchCategoriesByNamePaged(String name, Pageable pageable);

    /**
     * Get all products in a specific category.
     *
     * @param categoryId the ID of the category
     * @return a list of ProductResponseDTO
     */
    List<ProductResponseDTO> getProductsByCategory(Long categoryId);

    /**
     * Get all products in a specific category with pagination.
     *
     * @param categoryId the ID of the category
     * @param pageable   pagination information
     * @return a page of ProductResponseDTO
     */
    Slice<ProductResponseDTO> getProductsByCategoryPaged(Long categoryId, Pageable pageable);

    /**
     * Filter categories using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of categories matching the filter criteria
     */
    Page<CategoryResponseDTO> filterCategories(CategoryFilterDTO filter);

    /**
     * Filter categories using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of categories matching the filter criteria
     */
    Slice<CategoryResponseDTO> filterCategoriesWithSlice(CategoryFilterDTO filter);

    Slice<ProductResponseDTO> getProductsByCategoryWithFilters(
            Long categoryId, String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable);

    Slice<ProductResponseDTO> searchProductsByCategory(Long categoryId, String searchTerm, Pageable pageable);
}
