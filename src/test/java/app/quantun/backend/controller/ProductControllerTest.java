package app.quantun.backend.controller;

import app.quantun.backend.models.entity.Product;
import app.quantun.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));
        product.setInStock(true);
    }

    @Test
    @DisplayName("GET All Products - Success")
    void testGetAllProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(List.of(product));

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @DisplayName("GET Product By ID - Success")
    void testGetProductById() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("GET Product By ID - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST Create Product - Success")
    void testCreateProduct() throws Exception {
        // Arrange
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("PUT Update Product - Success")
    void testUpdateProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    @DisplayName("DELETE Product - Success")
    void testDeleteProduct() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET Products By Name - Success")
    void testSearchProductsByName() throws Exception {
        // Arrange
        when(productService.searchProductsByName("Test"))
                .thenReturn(List.of(product));

        // Act & Assert
        mockMvc.perform(get("/api/products/search")
                        .param("name", "Test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    @DisplayName("Validation - Create Product with Invalid Data")
    void testCreateProductValidationFailed() throws Exception {
        // Arrange
        Product invalidProduct = new Product();
        invalidProduct.setName(""); // Invalid empty name
        invalidProduct.setPrice(BigDecimal.valueOf(-10.0)); // Negative price

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

}