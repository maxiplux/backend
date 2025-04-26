package app.quantun.backend.service;

import app.quantun.backend.models.contract.request.ProductFilterDTO;
import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();

    Page<ProductResponseDTO> getAllProductsPaged(Pageable pageable);

    Optional<ProductResponseDTO> getProductById(Long id);

    @Transactional
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    @Transactional
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO);

    void updateProductFields(Product existingProduct, ProductRequestDTO productRequestDTO);

    @Transactional
    void deleteProduct(Long id);

    List<ProductResponseDTO> searchProductsByName(String name);

    Page<ProductResponseDTO> searchProductsByNamePaged(String name, Pageable pageable);

    List<ProductResponseDTO> getProductsUnderPrice(BigDecimal price);

    Page<ProductResponseDTO> getProductsUnderPricePaged(BigDecimal price, Pageable pageable);

    List<ProductResponseDTO> getInStockProducts();

    Page<ProductResponseDTO> getInStockProductsPaged(Pageable pageable);

    /**
     * Filter products using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of products matching the filter criteria
     */
    Page<ProductResponseDTO> filterProducts(ProductFilterDTO filter);

    /**
     * Filter products using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of products matching the filter criteria
     */
    Slice<ProductResponseDTO> filterProductsWithSlice(ProductFilterDTO filter);
}
