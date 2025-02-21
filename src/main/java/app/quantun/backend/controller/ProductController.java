package app.quantun.backend.controller;


import app.quantun.backend.models.entity.Product;
import app.quantun.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Product Management API",
        description = "Endpoints for managing product resources"
)
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(
            summary = "Retrieve all products",
            description = "Returns a list of all products in the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved products",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No products found"
            )
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return products.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a specific product by ID",
            description = "Returns a single product based on the provided ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<Product> getProductById(
            @Parameter(
                    description = "Unique identifier of the product",
                    required = true,
                    example = "1"
            )
            @PathVariable
            @Positive(message = "Product ID must be positive")
            Long id
    ) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create a new product",
            description = "Adds a new product to the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(
                                    name = "Product Creation Example",
                                    value = """
                    {
                        "name": "Smart Watch",
                        "description": "Advanced fitness tracker",
                        "price": 199.99,
                        "inStock": true
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data"
            )
    })
    public ResponseEntity<Product> createProduct(
            @Parameter(
                    description = "Product details",
                    required = true
            )
            @Valid @RequestBody Product product
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing product",
            description = "Updates the details of an existing product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(
                    description = "Product ID to update",
                    required = true,
                    example = "1"
            )
            @PathVariable
            @Positive(message = "Product ID must be positive")
            Long id,

            @Parameter(description = "Updated product details")
            @Valid @RequestBody Product productDetails
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, productDetails));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a product",
            description = "Removes a product from the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(
                    description = "Product ID to delete",
                    required = true,
                    example = "1"
            )
            @PathVariable
            @Positive(message = "Product ID must be positive")
            Long id
    ) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search products by name",
            description = "Finds products containing the given name"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved matching products",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            )
    })
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(
                    description = "Partial or full product name to search",
                    required = true,
                    example = "Smart"
            )
            @RequestParam String name
    ) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/under-price")
    @Operation(
            summary = "Find products under a specific price",
            description = "Retrieves all products priced below the given value"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved products under price",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            )
    })
    public ResponseEntity<List<Product>> getProductsUnderPrice(
            @Parameter(
                    description = "Maximum price threshold",
                    required = true,
                    example = "100.00"
            )
            @RequestParam
            BigDecimal price
    ) {
        return ResponseEntity.ok(productService.getProductsUnderPrice(price));
    }

    @GetMapping("/in-stock")
    @Operation(
            summary = "Retrieve in-stock products",
            description = "Returns all products currently in stock"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved in-stock products",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Product.class)
                    )
            )
    })
    public ResponseEntity<List<Product>> getInStockProducts() {
        return ResponseEntity.ok(productService.getInStockProducts());
    }
}