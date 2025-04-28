package app.quantun.backend.rest;

import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

/**
 * Test class for the ProductController.
 * This class contains unit tests for the ProductController.
 */
@WebMvcTest(ProductRestController.class)
public class ProductRestControllerTest {

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
                .description("This is a test product description")
                .price(BigDecimal.valueOf(19.99))
                .inStock(true)
                .build();
    }

    /**
     * Test for retrieving all products.
     * This test verifies that the getAllProducts endpoint returns a list of products.
     */
    @Test
    void testGetAllProducts() throws Exception {
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    /**
     * Test for retrieving a product by its ID.
     * This test verifies that the getProductById endpoint returns the correct product.
     */
    @Test
    void testGetProductById() throws Exception {
        Long productId = 1L;

        // Wrap the response in Optional.of()
        when(productService.getProductById(productId)).thenReturn(Optional.of(sampleProductResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Product"));
    }

    /**
     * Test for creating a new product.
     * This test verifies that the createProduct endpoint creates a new product.
     */
    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(ProductRequestDTO.class)))
                .thenReturn(sampleProductResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProductRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    /**
     * Test for updating an existing product.
     * This test verifies that the updateProduct endpoint updates the product details.
     */
    @Test
    void testUpdateProduct() throws Exception {
        Long productId = 1L;

        when(productService.updateProduct(eq(productId), any(ProductRequestDTO.class)))
                .thenReturn(sampleProductResponse);

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProductRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    /**
     * Test for searching products by name.
     * This test verifies that the searchProductsByName endpoint returns matching products.
     */
    @Test
    void testSearchProductsByName() throws Exception {
        String searchName = "Test";
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.searchProductsByName(searchName)).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/search")
                        .param("name", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    /**
     * Test for retrieving products under a specific price.
     * This test verifies that the getProductsUnderPrice endpoint returns products below the given price.
     */
    @Test
    void testGetProductsUnderPrice() throws Exception {
        BigDecimal maxPrice = BigDecimal.valueOf(20);
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getProductsUnderPrice(maxPrice)).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/under-price")
                        .param("price", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].price").value(19.99));
    }

    /**
     * Test for retrieving products that are in stock.
     * This test verifies that the getInStockProducts endpoint returns products that are in stock.
     */
    @Test
    void testGetInStockProducts() throws Exception {
        List<ProductResponseDTO> products = Arrays.asList(sampleProductResponse);

        when(productService.getInStockProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/products/in-stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stock").value(10));
    }
}
