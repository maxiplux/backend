package app.quantun.backend.rest;

import app.quantun.backend.models.contract.request.CategoryFilterDTO;
import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.models.contract.response.CategoryResponseDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing categories.
 * This class provides endpoints for CRUD operations on categories.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Operations for managing categories")
public class CategoryRestController {
    private final CategoryService categoryService;

    /**
     * Retrieve a list of all categories.
     *
     * @return a list of CategoryResponseDTO
     */
    @GetMapping
    @Operation(summary = "Get all categories",
            description = "Retrieve a list of all categories",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Retrieve a paged list of all categories.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of CategoryResponseDTO
     */
    @GetMapping("/paged")
    @Operation(summary = "Get all categories with pagination",
            description = "Retrieve a paged list of all categories",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<Page<CategoryResponseDTO>> getAllCategoriesPaged(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(categoryService.getAllCategoriesPaged(pageable));
    }

    /**
     * Retrieve a specific category by its ID.
     *
     * @param id the ID of the category
     * @return the CategoryResponseDTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID",
            description = "Retrieve a specific category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add a new category to the system.
     *
     * @param categoryRequestDTO the details of the category to be created
     * @return the created CategoryResponseDTO
     */
    @PostMapping
    @Operation(summary = "Create a new category",
            description = "Add a new category to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Parameter(description = "Category details", required = true)
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequestDTO));
    }

    /**
     * Update details of an existing category.
     *
     * @param id the ID of the category to be updated
     * @param categoryRequestDTO the updated category details
     * @return the updated CategoryResponseDTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category",
            description = "Update details of an existing category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated category details", required = true)
            @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryRequestDTO));
    }

    /**
     * Remove a category from the system.
     *
     * @param id the ID of the category to be deleted
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category",
            description = "Remove a category from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
            })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find categories containing the given name.
     *
     * @param name the name to search for
     * @return a list of CategoryResponseDTO
     */
    @GetMapping("/search")
    @Operation(summary = "Search categories by name",
            description = "Find categories containing the given name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<List<CategoryResponseDTO>> searchCategoriesByName(
            @Parameter(description = "Category name to search", example = "Electronics")
            @RequestParam String name) {
        return ResponseEntity.ok(categoryService.searchCategoriesByName(name));
    }

    /**
     * Find categories containing the given name with pagination.
     *
     * @param name the name to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of CategoryResponseDTO
     */
    @GetMapping("/search/paged")
    @Operation(summary = "Search categories by name with pagination",
            description = "Find categories containing the given name with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<Page<CategoryResponseDTO>> searchCategoriesByNamePaged(
            @Parameter(description = "Category name to search", example = "Electronics")
            @RequestParam String name,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(categoryService.searchCategoriesByNamePaged(name, pageable));
    }

    /**
     * Get all products in a specific category.
     *
     * @param id the ID of the category
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/{id}/products")
    @Operation(summary = "Get products by category",
            description = "Retrieve all products in a specific category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
    }

    /**
     * Get all products in a specific category with pagination.
     *
     * @param id the ID of the category
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of ProductResponseDTO
     */
    @GetMapping("/{id}/products/paged")
    @Operation(summary = "Get products by category with pagination",
            description = "Retrieve all products in a specific category with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    public ResponseEntity<Slice<ProductResponseDTO>> getProductsByCategoryPaged(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "name")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(categoryService.getProductsByCategoryPaged(id, pageable));
    }

    /**
     * Filter categories using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of categories matching the filter criteria
     */
    @PostMapping("/filter")
    @Operation(summary = "Filter categories with criteria",
            description = "Filter categories using various criteria with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully filtered categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<Page<CategoryResponseDTO>> filterCategories(
            @Parameter(description = "Filter criteria", required = true)
            @Valid @RequestBody CategoryFilterDTO filter) {

        return ResponseEntity.ok(categoryService.filterCategories(filter));
    }

    /**
     * Filter categories using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of categories matching the filter criteria
     */
    @PostMapping("/filter/slice")
    @Operation(summary = "Filter categories with criteria using slice",
            description = "Filter categories using various criteria with slice-based pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully filtered categories",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CategoryResponseDTO.class)))
            })
    public ResponseEntity<Slice<CategoryResponseDTO>> filterCategoriesWithSlice(
            @Parameter(description = "Filter criteria", required = true)
            @Valid @RequestBody CategoryFilterDTO filter) {

        return ResponseEntity.ok(categoryService.filterCategoriesWithSlice(filter));
    }
}
