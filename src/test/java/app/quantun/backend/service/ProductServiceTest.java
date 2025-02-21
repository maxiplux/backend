package app.quantun.backend.service;


import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    @DisplayName("Get All Products - Success")
    void testGetAllProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product));

        // Act
        List<Product> products = productService.getAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(1, products.size());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Get Product By ID - Success")
    void testGetProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> foundProduct = productService.getProductById(1L);

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getId(), foundProduct.get().getId());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Create Product - Success")
    void testCreateProduct() {
        // Arrange
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product createdProduct = productService.createProduct(product);

        // Assert
        assertNotNull(createdProduct);
        assertEquals(product.getName(), createdProduct.getName());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Update Product - Success")
    void testUpdateProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(BigDecimal.valueOf(29.99));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertEquals("Updated Product", result.getName());
        assertEquals(BigDecimal.valueOf(29.99), result.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Update Product - Not Found")
    void testUpdateProductNotFound() {
        // Arrange
        Product updatedProduct = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, updatedProduct);
        });
    }

    @Test
    @DisplayName("Delete Product - Success")
    void testDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).delete(product);
    }

    @Test
    @DisplayName("Search Products By Name")
    void testSearchProductsByName() {
        // Arrange
        when(productRepository.findByNameContaining("Test"))
                .thenReturn(List.of(product));

        // Act
        List<Product> results = productService.searchProductsByName("Test");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(productRepository).findByNameContaining("Test");
    }

    @Test
    @DisplayName("Get Products Under Price")
    void testGetProductsUnderPrice() {
        // Arrange
        BigDecimal price = BigDecimal.valueOf(20.00);
        when(productRepository.findByPriceLessThan(price))
                .thenReturn(List.of(product));

        // Act
        List<Product> results = productService.getProductsUnderPrice(price);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(productRepository).findByPriceLessThan(price);
    }
}