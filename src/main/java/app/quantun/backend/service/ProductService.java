package app.quantun.backend.service;

import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Page<ProductResponseDTO> getAllProducts(Pageable pageable);

    Optional<ProductResponseDTO> getProductById(Long id);

    @Transactional
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    @Transactional
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void updateProductFields(Product existingProduct, ProductRequestDTO productRequestDTO);

    @Transactional
    void deleteProduct(Long id);

    List<ProductResponseDTO> searchProductsByName(String name);

    List<ProductResponseDTO> getProductsUnderPrice(BigDecimal price);

    List<ProductResponseDTO> getInStockProducts();
}
