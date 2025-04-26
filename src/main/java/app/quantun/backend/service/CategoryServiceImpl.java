package app.quantun.backend.service;

import app.quantun.backend.exception.CategoryNotFoundException;
import app.quantun.backend.models.contract.request.CategoryFilterDTO;
import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.models.contract.response.CategoryResponseDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Category;
import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.CategoryRepository;
import app.quantun.backend.repository.ProductRepository;
import app.quantun.backend.repository.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing categories.
 * This class provides methods for CRUD operations on categories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    /**
     * Retrieve a list of all categories.
     *
     * @return a list of CategoryResponseDTO
     */
    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Retrieving all categories");
        List<CategoryResponseDTO> categories = categoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Retrieved {} categories", categories.size());
        return categories;
    }

    /**
     * Retrieve a specific category by its ID.
     *
     * @param id the ID of the category
     * @return an Optional containing the CategoryResponseDTO if found, otherwise empty
     */
    @Override
    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        log.info("Retrieving category with id: {}", id);
        Optional<CategoryResponseDTO> category = categoryRepository.findById(id)
                .map(c -> {
                    log.debug("Found category: {}", c.getName());
                    return modelMapper.map(c, CategoryResponseDTO.class);
                });

        if (category.isEmpty()) {
            log.warn("Category with id {} not found", id);
        }

        return category;
    }

    /**
     * Add a new category to the system.
     *
     * @param categoryRequestDTO the details of the category to be created
     * @return the created CategoryResponseDTO
     */
    @Transactional
    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        log.info("Creating new category: {}", categoryRequestDTO.getName());
        Category category = modelMapper.map(categoryRequestDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created with id: {}", savedCategory.getId());
        return modelMapper.map(savedCategory, CategoryResponseDTO.class);
    }

    /**
     * Update details of an existing category.
     *
     * @param id                 the ID of the category to be updated
     * @param categoryRequestDTO the updated category details
     * @return the updated CategoryResponseDTO
     */
    @Transactional
    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryRequestDTO) {
        log.info("Updating category with id: {}", id);
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    log.debug("Found category to update: {}", existingCategory.getName());
                    updateCategoryFields(existingCategory, categoryRequestDTO);
                    Category updatedCategory = categoryRepository.save(existingCategory);
                    log.info("Category updated successfully: {}", updatedCategory.getId());
                    return modelMapper.map(updatedCategory, CategoryResponseDTO.class);
                })
                .orElseThrow(() -> {
                    log.error("Failed to update - category not found with id: {}", id);
                    return new CategoryNotFoundException("Category not found with id " + id);
                });
    }

    /**
     * Update the fields of an existing category with the provided details.
     *
     * @param existingCategory   the existing category to be updated
     * @param categoryRequestDTO the updated category details
     */
    @Override
    public void updateCategoryFields(Category existingCategory, CategoryRequestDTO categoryRequestDTO) {
        log.debug("Updating fields for category: {}", existingCategory.getId());
        if (categoryRequestDTO.getName() != null) {
            log.debug("Updating name from '{}' to '{}'", existingCategory.getName(), categoryRequestDTO.getName());
            existingCategory.setName(categoryRequestDTO.getName());
        }
        if (categoryRequestDTO.getDescription() != null) {
            log.debug("Updating description for category: {}", existingCategory.getId());
            existingCategory.setDescription(categoryRequestDTO.getDescription());
        }
    }

    /**
     * Remove a category from the system.
     *
     * @param id the ID of the category to be deleted
     */
    @Transactional
    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete - category not found with id: {}", id);
                    return new CategoryNotFoundException("Category not found with id " + id);
                });
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }

    /**
     * Find categories containing the given name.
     *
     * @param name the name to search for
     * @return a list of CategoryResponseDTO
     */
    @Override
    public List<CategoryResponseDTO> searchCategoriesByName(String name) {
        log.info("Searching categories by name: {}", name);
        List<CategoryResponseDTO> categories = categoryRepository.findByNameContaining(name).stream()
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Found {} categories matching name: {}", categories.size(), name);
        return categories;
    }

    /**
     * Get all products in a specific category.
     *
     * @param categoryId the ID of the category
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        log.info("Retrieving products for category with id: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    List<ProductResponseDTO> products = category.getProducts().stream()
                            .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                            .collect(Collectors.toList());
                    log.info("Found {} products in category: {}", products.size(), category.getName());
                    return products;
                })
                .orElseThrow(() -> {
                    log.error("Failed to retrieve products - category not found with id: {}", categoryId);
                    return new CategoryNotFoundException("Category not found with id " + categoryId);
                });
    }

    /**
     * Retrieve a paged list of all categories.
     *
     * @param pageable pagination information
     * @return a page of CategoryResponseDTO
     */
    @Override
    public Page<CategoryResponseDTO> getAllCategoriesPaged(Pageable pageable) {
        log.info("Retrieving paged categories with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<CategoryResponseDTO> categoryPage = categoryRepository.findAll(pageable)
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class));
        log.info("Retrieved page {} of {} with {} categories",
                categoryPage.getNumber(), categoryPage.getTotalPages(), categoryPage.getNumberOfElements());
        return categoryPage;
    }

    /**
     * Find categories containing the given name with pagination.
     *
     * @param name     the name to search for
     * @param pageable pagination information
     * @return a page of CategoryResponseDTO
     */
    @Override
    public Page<CategoryResponseDTO> searchCategoriesByNamePaged(String name, Pageable pageable) {
        log.info("Searching paged categories by name: {} with page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());
        Page<Category> categoryPage = categoryRepository.findByNameContaining(name, pageable);
        Page<CategoryResponseDTO> result = categoryPage
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class));
        log.info("Found page {} of {} with {} categories matching name: {}",
                result.getNumber(), result.getTotalPages(), result.getNumberOfElements(), name);
        return result;
    }

    /**
     * Get all products in a specific category with pagination.
     *
     * @param categoryId the ID of the category
     * @param pageable   pagination information
     * @return a page of ProductResponseDTO
     */
    @Override
    public Slice<ProductResponseDTO> getProductsByCategoryPaged(Long categoryId, Pageable pageable) {
        log.info("Retrieving paged products for category with id: {} with page: {}, size: {}",
                categoryId, pageable.getPageNumber(), pageable.getPageSize());

        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Failed to retrieve paged products - category not found with id: {}", categoryId);
            throw new CategoryNotFoundException("Category not found with id " + categoryId);
        }

        // Use the JPQL query to get paginated results directly from the database
        Slice<Product> productSlice = productRepository.findProductsByCategoryIdSliced(categoryId, pageable);

        // Map to DTOs
        return productSlice.map(product -> modelMapper.map(product, ProductResponseDTO.class));
    }

    /**
     * Filter categories using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of categories matching the filter criteria
     */
    @Override
    public Page<CategoryResponseDTO> filterCategories(CategoryFilterDTO filter) {
        log.info("Filtering categories with criteria: {}", filter);

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDirection(), filter.getSortBy())
        );

        // Apply specification and pagination
        Page<Category> categoryPage = categoryRepository.findAll(
                CategorySpecification.getCategorySpecification(filter),
                pageable
        );

        // Map to DTOs
        Page<CategoryResponseDTO> responsePage = categoryPage.map(
                category -> modelMapper.map(category, CategoryResponseDTO.class)
        );

        log.info("Filtered {} categories (page {} of {})",
                responsePage.getNumberOfElements(),
                responsePage.getNumber() + 1,
                responsePage.getTotalPages());

        return responsePage;
    }

    /**
     * Filter categories using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of categories matching the filter criteria
     */
    @Override
    public Slice<CategoryResponseDTO> filterCategoriesWithSlice(CategoryFilterDTO filter) {
        log.info("Filtering categories with criteria using slice: {}", filter);

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDirection(), filter.getSortBy())
        );

        // Apply specification and pagination
        Slice<Category> categorySlice = categoryRepository.findAll(
                CategorySpecification.getCategorySpecification(filter),
                pageable
        );

        // Map to DTOs
        Slice<CategoryResponseDTO> responseSlice = categorySlice.map(
                category -> modelMapper.map(category, CategoryResponseDTO.class)
        );

        log.info("Filtered {} categories (slice page {})",
                responseSlice.getNumberOfElements(),
                responseSlice.getNumber() + 1);

        return responseSlice;
    }

    @Override
    public Slice<ProductResponseDTO> getProductsByCategoryWithFilters(
            Long categoryId, String name, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, Pageable pageable) {

        log.info("Retrieving filtered products for category with id: {}", categoryId);

        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Failed to retrieve paged products - category not found with id: {}", categoryId);
            throw new CategoryNotFoundException("Category not found with id " + categoryId);
        }

        // Use the JPQL query with filters
        Slice<Product> productSlice = productRepository.findProductsByCategoryWithFilters(
                categoryId, name, minPrice, maxPrice, inStock, pageable);

        // Map to DTOs
        return productSlice.map(product -> modelMapper.map(product, ProductResponseDTO.class));
    }

    @Override
    public Slice<ProductResponseDTO> searchProductsByCategory(Long categoryId, String searchTerm, Pageable pageable) {
        log.info("Searching products in category with id: {} using search term: {}", categoryId, searchTerm);

        // Use the text search JPQL query
        Slice<Product> productSlice = productRepository.findProductsByCategoryIdWithTextSearch(
                categoryId, searchTerm, pageable);

        // Map to DTOs
        return productSlice.map(product -> modelMapper.map(product, ProductResponseDTO.class));
    }
}
