package app.quantun.backend.controller;

import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)

class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductRequestDTO sampleProductRequest;

    @BeforeEach
    void setUp() {
        sampleProductRequest = new ProductRequestDTO();
        sampleProductRequest.setName("Test Product");
        sampleProductRequest.setDescription("This is a test product description");
        sampleProductRequest.setPrice(new BigDecimal("10.0"));
    }

    @Test
    void testListProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));

        verify(productService).getAllProducts();
    }

    @Test
    void testShowCreateForm() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", new ProductRequestDTO()));
    }

    @Test
    void testCreateProduct_Success() throws Exception {
        // Arrange
        // Replace doNothing() with when() if the method returns a value
        when(productService.createProduct(any(ProductRequestDTO.class))).thenReturn(new ProductResponseDTO());

        // Act & Assert
        mockMvc.perform(post("/products")
                        .flashAttr("product", sampleProductRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"))
                .andExpect(flash().attribute("message", "Product created successfully"));

        verify(productService).createProduct(any(ProductRequestDTO.class));

    }

    @Test
    void testCreateProduct_ValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                        .flashAttr("product", new ProductRequestDTO())) // Empty DTO will trigger validation errors
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));

        verify(productService, never()).createProduct(any(ProductRequestDTO.class));
    }

    @Test
    void testShowEditForm() throws Exception {
        // Arrange
        Long productId = 1L;
        // Create a mock ProductResponseDTO instead of using ProductRequestDTO
        ProductResponseDTO mockProductResponse = new ProductResponseDTO();
        mockProductResponse.setName(sampleProductRequest.getName());
        mockProductResponse.setPrice(sampleProductRequest.getPrice());

        when(productService.getProductById(anyLong())).thenReturn(Optional.of(mockProductResponse));

        // Act & Assert
        mockMvc.perform(get("/products/{id}/edit", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("product"));

        verify(productService).getProductById(productId);

    }

    @Test
    void testShowEditForm_ProductNotFound() throws Exception {
        // Arrange
        Long productId = 1L;
        when(productService.getProductById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/products/{id}/edit", productId))
                .andExpect(status().is4xxClientError());

        verify(productService).getProductById(productId);
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        // Arrange
        Long productId = 1L;
        // Use when() instead of doNothing()
        when(productService.updateProduct(anyLong(), any(ProductRequestDTO.class)))
                .thenReturn(new ProductResponseDTO()); // Assuming it returns a ProductResponseDTO

        // Act & Assert
        mockMvc.perform(post("/products/{id}", productId)
                        .flashAttr("product", sampleProductRequest))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"))
                .andExpect(flash().attribute("message", "Product updated successfully"));

        verify(productService).updateProduct(eq(productId), any(ProductRequestDTO.class));

    }

    @Test
    void testUpdateProduct_ValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products/{id}", 1L)
                        .flashAttr("product", new ProductRequestDTO())) // Empty DTO will trigger validation errors
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"));

        verify(productService, never()).updateProduct(anyLong(), any(ProductRequestDTO.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Arrange
        Long productId = 1L;
        doNothing().when(productService).deleteProduct(anyLong());

        // Act & Assert
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products"))
                .andExpect(flash().attribute("message", "Product deleted successfully"));

        verify(productService).deleteProduct(productId);
    }

    @Test
    void testSearchProducts() throws Exception {
        // Arrange
        String searchName = "Test";
        when(productService.searchProductsByName(anyString())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));

        verify(productService).searchProductsByName(searchName);
    }

}
