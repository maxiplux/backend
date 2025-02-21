package app.quantun.backend.service;

import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for the ProductService.
 * This class contains unit tests for the ProductService.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequestDTO testProductRequestDTO;
    private ProductResponseDTO testProductResponseDTO;

    @BeforeEach
    void setUp() {
        // Initialize test data
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.00));
        testProduct.setInStock(true);

        testProductRequestDTO = new ProductRequestDTO();
        testProductRequestDTO.setName("Test Product");
        testProductRequestDTO.setPrice(BigDecimal.valueOf(100.00));
        testProductRequestDTO.setInStock(true);

        testProductResponseDTO = new ProductResponseDTO();
        testProductResponseDTO.setId(1L);
        testProductResponseDTO.setName("Test Product");
        testProductResponseDTO.setPrice(BigDecimal.valueOf(100.00));
        testProductResponseDTO.setInStock(true);
    }

    /**
     * Test for retrieving all products.
     * This test verifies that the getAllProducts method returns a list of products.
     */
    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        when(modelMapper.map(any(), eq(ProductResponseDTO.class))).thenReturn(testProductResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    /**
     * Test for retrieving a product by its ID.
     * This test verifies that the getProductById method returns the correct product.
     */
    @Test
    void testGetProductById_Existing() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(modelMapper.map(testProduct, ProductResponseDTO.class)).thenReturn(testProductResponseDTO);

        // Act
        Optional<ProductResponseDTO> result = productService.getProductById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProductResponseDTO, result.get());
    }

    /**
     * Test for retrieving a product by its ID when the product is not found.
     * This test verifies that the getProductById method returns an empty Optional.
     */
    @Test
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<ProductResponseDTO> result = productService.getProductById(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Test for creating a new product.
     * This test verifies that the createProduct method creates a new product.
     */
    @Test
    void testCreateProduct() {
        // Arrange
        when(modelMapper.map(testProductRequestDTO, Product.class)).thenReturn(testProduct);
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(modelMapper.map(testProduct, ProductResponseDTO.class)).thenReturn(testProductResponseDTO);

        // Act
        ProductResponseDTO result = productService.createProduct(testProductRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testProductResponseDTO, result);
        verify(productRepository).save(testProduct);
    }

    /**
     * Test for updating an existing product.
     * This test verifies that the updateProduct method updates the product details.
     */
    @Test
    void testUpdateProduct_Existing() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setName("Old Name");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(modelMapper.map(existingProduct, ProductResponseDTO.class)).thenReturn(testProductResponseDTO);

        // Act
        ProductResponseDTO result = productService.updateProduct(1L, testProductRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testProductResponseDTO, result);
        verify(productRepository).save(existingProduct);
    }

    /**
     * Test for updating a product when the product is not found.
     * This test verifies that the updateProduct method throws a RuntimeException.
     */
    @Test
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                productService.updateProduct(999L, testProductRequestDTO)
        );
    }

    /**
     * Test for deleting an existing product.
     * This test verifies that the deleteProduct method deletes the product.
     */
    @Test
    void testDeleteProduct_Existing() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).delete(testProduct);
    }

    /**
     * Test for deleting a product when the product is not found.
     * This test verifies that the deleteProduct method throws a RuntimeException.
     */
    @Test
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                productService.deleteProduct(999L)
        );
    }

    /**
     * Test for searching products by name.
     * This test verifies that the searchProductsByName method returns matching products.
     */
    @Test
    void testSearchProductsByName() {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productRepository.findByNameContaining("Test")).thenReturn(products);
        when(modelMapper.map(any(), eq(ProductResponseDTO.class))).thenReturn(testProductResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.searchProductsByName("Test");

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    /**
     * Test for retrieving products under a specific price.
     * This test verifies that the getProductsUnderPrice method returns products below the given price.
     */
    @Test
    void testGetProductsUnderPrice() {
        // Arrange
        List<Product> products = List.of(testProduct);
        BigDecimal maxPrice = BigDecimal.valueOf(200.00);
        when(productRepository.findByPriceLessThan(maxPrice)).thenReturn(products);
        when(modelMapper.map(any(), eq(ProductResponseDTO.class))).thenReturn(testProductResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.getProductsUnderPrice(maxPrice);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    /**
     * Test for retrieving products that are in stock.
     * This test verifies that the getInStockProducts method returns products that are in stock.
     */
    @Test
    void testGetInStockProducts() {
        // Arrange
        List<Product> products = List.of(testProduct);
        when(productRepository.findByInStock(true)).thenReturn(products);
        when(modelMapper.map(any(), eq(ProductResponseDTO.class))).thenReturn(testProductResponseDTO);

        // Act
        List<ProductResponseDTO> result = productService.getInStockProducts();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

}
