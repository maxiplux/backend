package app.quantun.backend.controller;

import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductResponseDTO sampleProductResponse;
    private ProductRequestDTO sampleProductRequest;

    @BeforeEach
    void setUp() {
        // Create sample DTOs for testing
        sampleProductResponse = ProductResponseDTO.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(19.99))
                .stock(10)
                .build();

        sampleProductRequest = ProductRequestDTO.builder()
                .name("Test Product")
                .price(BigDecimal.valueOf(19.99))
                .inStock(true)
                .build();
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void testGetProductById() throws Exception {
/*
        Long productId = 1L;

        // Wrap the response in Optional.of()
        when(productService.getProductById(productId)).thenReturn(Optional.of(sampleProductResponse));

        mockMvc.perform(get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
*/

        Long productId = 1L;

// Wrap the response in Optional.of()
        when(productService.getProductById(productId)).thenReturn(Optional.of(sampleProductResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(sampleProductResponse);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Long productId = 1L;

        when(productService.updateProduct(eq(productId), any(ProductRequestDTO.class)))
                .thenReturn(sampleProductResponse);

        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProductRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testSearchProductsByName() throws Exception {
        String searchName = "Test";
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.searchProductsByName(searchName)).thenReturn(products);

        mockMvc.perform(get("/api/products/search")
                        .param("name", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void testGetProductsUnderPrice() throws Exception {
        BigDecimal maxPrice = BigDecimal.valueOf(20);
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getProductsUnderPrice(maxPrice)).thenReturn(products);

        mockMvc.perform(get("/api/products/under-price")
                        .param("price", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    @Test
    void testGetInStockProducts() throws Exception {
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getInStockProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products/in-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stock").value(10));
    }
}