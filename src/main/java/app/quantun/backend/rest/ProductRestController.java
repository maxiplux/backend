package app.quantun.backend.rest;

import app.quantun.backend.models.contract.request.ProductFilterDTO;
import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.service.ProductService;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller class for managing products.
 * This class provides endpoints for CRUD operations on products.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Operations for managing products")
public class ProductRestController {
    private final ProductService productService;

    /**
     * Retrieve a list of all products.
     *
     * @return a list of ProductResponseDTO
     */
    @GetMapping
    @Operation(summary = "Get all products",
            description = "Retrieve a list of all products",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieve a paged list of all products.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of ProductResponseDTO
     */
    @GetMapping("/paged")
    @Operation(summary = "Get all products with pagination",
            description = "Retrieve a paged list of all products",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> getAllProductsPaged(
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

        return ResponseEntity.ok(productService.getAllProductsPaged(pageable));
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * @param id the ID of the product
     * @return the ProductResponseDTO
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID",
            description = "Retrieve a specific product by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found")
            })
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add a new product to the system.
     *
     * @param productRequestDTO the details of the product to be created
     * @return the created ProductResponseDTO
     */
    @PostMapping
    @Operation(summary = "Create a new product",
            description = "Add a new product to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Parameter(description = "Product details", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequestDTO));
    }

    /**
     * Update details of an existing product.
     *
     * @param id the ID of the product to be updated
     * @param productRequestDTO the updated product details
     * @return the updated ProductResponseDTO
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product",
            description = "Update details of an existing product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product updated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated product details", required = true)
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequestDTO));
    }

    /**
     * Remove a product from the system.
     *
     * @param id the ID of the product to be deleted
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product",
            description = "Remove a product from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
            })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", example = "1")
            @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/search")
    @Operation(summary = "Search products by name",
            description = "Find products containing the given name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> searchProductsByName(
            @Parameter(description = "Product name to search", example = "Phone")
            @RequestParam String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    /**
     * Find products containing the given name with pagination.
     *
     * @param name the name to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of ProductResponseDTO
     */
    @GetMapping("/search/paged")
    @Operation(summary = "Search products by name with pagination",
            description = "Find products containing the given name with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved matching products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> searchProductsByNamePaged(
            @Parameter(description = "Product name to search", example = "Phone")
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

        return ResponseEntity.ok(productService.searchProductsByNamePaged(name, pageable));
    }

    /**
     * Retrieve products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/under-price")
    @Operation(summary = "Get products under a specific price",
            description = "Retrieve products priced below a given value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getProductsUnderPrice(
            @Parameter(description = "Maximum price", example = "100.00")
            @RequestParam BigDecimal price) {
        return ResponseEntity.ok(productService.getProductsUnderPrice(price));
    }

    /**
     * Retrieve products priced below a given value with pagination.
     *
     * @param price the maximum price
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of ProductResponseDTO
     */
    @GetMapping("/under-price/paged")
    @Operation(summary = "Get products under a specific price with pagination",
            description = "Retrieve products priced below a given value with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> getProductsUnderPricePaged(
            @Parameter(description = "Maximum price", example = "100.00")
            @RequestParam BigDecimal price,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "price")
            @RequestParam(defaultValue = "price") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(productService.getProductsUnderPricePaged(price, pageable));
    }

    /**
     * Retrieve all products that are currently in stock.
     *
     * @return a list of ProductResponseDTO
     */
    @GetMapping("/in-stock")
    @Operation(summary = "Get products in stock",
            description = "Retrieve all products that are currently in stock",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved in-stock products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<List<ProductResponseDTO>> getInStockProducts() {
        return ResponseEntity.ok(productService.getInStockProducts());
    }

    /**
     * Retrieve all products that are currently in stock with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sort the field to sort by
     * @param direction the sort direction
     * @return a page of ProductResponseDTO
     */
    @GetMapping("/in-stock/paged")
    @Operation(summary = "Get products in stock with pagination",
            description = "Retrieve all products that are currently in stock with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved in-stock products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> getInStockProductsPaged(
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

        return ResponseEntity.ok(productService.getInStockProductsPaged(pageable));
    }

    /**
     * Filter products using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of products matching the filter criteria
     */
    @PostMapping("/filter")
    @Operation(summary = "Filter products with criteria",
            description = "Filter products using various criteria with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully filtered products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Page<ProductResponseDTO>> filterProducts(
            @Parameter(description = "Filter criteria", required = true)
            @Valid @RequestBody ProductFilterDTO filter) {

        return ResponseEntity.ok(productService.filterProducts(filter));
    }

    /**
     * Filter products using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of products matching the filter criteria
     */
    @PostMapping("/filter/slice")
    @Operation(summary = "Filter products with criteria using slice",
            description = "Filter products using various criteria with slice-based pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully filtered products",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponseDTO.class)))
            })
    public ResponseEntity<Slice<ProductResponseDTO>> filterProductsWithSlice(
            @Parameter(description = "Filter criteria", required = true)
            @Valid @RequestBody ProductFilterDTO filter) {

        return ResponseEntity.ok(productService.filterProductsWithSlice(filter));
    }
}
