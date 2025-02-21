package app.quantun.backend.service;


import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setInStock(productDetails.isInStock());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        productRepository.delete(product);
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> getProductsUnderPrice(BigDecimal price) {
        return productRepository.findByPriceLessThan(price);
    }

    public List<Product> getInStockProducts() {
        return productRepository.findByInStock(true);
    }
}

