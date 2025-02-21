package app.quantun.backend.rest;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Operations for managing products")
public class ProductController {
    private final ProductService productService;

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
}