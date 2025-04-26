package app.quantun.backend.service;

import app.quantun.backend.exception.ProductNotFoundException;
import app.quantun.backend.models.contract.request.ProductFilterDTO;
import app.quantun.backend.models.contract.request.ProductRequestDTO;
import app.quantun.backend.models.contract.response.ProductResponseDTO;
import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.ProductRepository;
import app.quantun.backend.repository.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing products.
 * This class provides methods for CRUD operations on products.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    /**
     * Retrieve a list of all products.
     *
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getAllProducts() {
        log.info("Retrieving all products");
        List<ProductResponseDTO> products = productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Retrieved {} products", products.size());
        return products;
    }

    /**
     * Retrieve a specific product by its ID.
     *
     * @param id the ID of the product
     * @return an Optional containing the ProductResponseDTO if found, otherwise empty
     */
    @Override
    public Optional<ProductResponseDTO> getProductById(Long id) {
        log.info("Retrieving product with id: {}", id);
        Optional<ProductResponseDTO> product = productRepository.findById(id)
                .map(p -> {
                    log.debug("Found product: {}", p.getName());
                    return modelMapper.map(p, ProductResponseDTO.class);
                });

        if (product.isEmpty()) {
            log.warn("Product with id {} not found", id);
        }

        return product;
    }

    /**
     * Add a new product to the system.
     *
     * @param productRequestDTO the details of the product to be created
     * @return the created ProductResponseDTO
     */
    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        log.info("Creating new product: {}", productRequestDTO.getName());
        Product product = modelMapper.map(productRequestDTO, Product.class);
        Product savedProduct = productRepository.save(product);
        log.info("Product created with id: {}", savedProduct.getId());
        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    /**
     * Update details of an existing product.
     *
     * @param id                the ID of the product to be updated
     * @param productRequestDTO the updated product details
     * @return the updated ProductResponseDTO
     */
    @Transactional
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        log.info("Updating product with id: {}", id);
        return productRepository.findById(id)
                .map(existingProduct -> {
                    log.debug("Found product to update: {}", existingProduct.getName());
                    updateProductFields(existingProduct, productRequestDTO);
                    Product updatedProduct = productRepository.save(existingProduct);
                    log.info("Product updated successfully: {}", updatedProduct.getId());
                    return modelMapper.map(updatedProduct, ProductResponseDTO.class);
                })
                .orElseThrow(() -> {
                    log.error("Failed to update - product not found with id: {}", id);
                    return new ProductNotFoundException("Product not found with id " + id);
                });
    }

    /**
     * Update the fields of an existing product with the provided details.
     *
     * @param existingProduct   the existing product to be updated
     * @param productRequestDTO the updated product details
     */
    @Override
    public void updateProductFields(Product existingProduct, ProductRequestDTO productRequestDTO) {
        log.debug("Updating fields for product: {}", existingProduct.getId());
        if (productRequestDTO.getName() != null) {
            log.debug("Updating name from '{}' to '{}'", existingProduct.getName(), productRequestDTO.getName());
            existingProduct.setName(productRequestDTO.getName());
        }
        if (productRequestDTO.getDescription() != null) {
            log.debug("Updating description for product: {}", existingProduct.getId());
            existingProduct.setDescription(productRequestDTO.getDescription());
        }
        if (productRequestDTO.getPrice() != null) {
            log.debug("Updating price from '{}' to '{}'", existingProduct.getPrice(), productRequestDTO.getPrice());
            existingProduct.setPrice(productRequestDTO.getPrice());
        }
        log.debug("Updating stock status from '{}' to '{}'", existingProduct.isInStock(), productRequestDTO.isInStock());
        existingProduct.setInStock(productRequestDTO.isInStock());

        if (productRequestDTO.getStock() > 0) {
            log.debug("Updating stock quantity to: {}", productRequestDTO.getStock());
            existingProduct.setStock(productRequestDTO.getStock());
        }
    }

    /**
     * Remove a product from the system.
     *
     * @param id the ID of the product to be deleted
     */
    @Transactional
    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete - product not found with id: {}", id);
                    return new ProductNotFoundException("Product not found with id " + id);
                });
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", id);
    }

    /**
     * Find products containing the given name.
     *
     * @param name the name to search for
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> searchProductsByName(String name) {
        log.info("Searching products by name: {}", name);
        List<ProductResponseDTO> products = productRepository.findByNameContaining(name).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Found {} products matching name: {}", products.size(), name);
        return products;
    }

    /**
     * Retrieve products priced below a given value.
     *
     * @param price the maximum price
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getProductsUnderPrice(BigDecimal price) {
        log.info("Retrieving products under price: {}", price);
        List<ProductResponseDTO> products = productRepository.findByPriceLessThan(price).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Found {} products under price: {}", products.size(), price);
        return products;
    }

    /**
     * Retrieve all products that are currently in stock.
     *
     * @return a list of ProductResponseDTO
     */
    @Override
    public List<ProductResponseDTO> getInStockProducts() {
        log.info("Retrieving in-stock products");
        List<ProductResponseDTO> products = productRepository.findByInStock(true).stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
        log.info("Found {} in-stock products", products.size());
        return products;
    }

    /**
     * Retrieve a paged list of all products.
     *
     * @param pageable pagination information
     * @return a page of ProductResponseDTO
     */
    @Override
    public Page<ProductResponseDTO> getAllProductsPaged(Pageable pageable) {
        log.info("Retrieving paged products with page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ProductResponseDTO> productPage = productRepository.findAll(pageable)
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
        log.info("Retrieved page {} of {} with {} products",
                productPage.getNumber(), productPage.getTotalPages(), productPage.getNumberOfElements());
        return productPage;
    }

    /**
     * Find products containing the given name with pagination.
     *
     * @param name     the name to search for
     * @param pageable pagination information
     * @return a page of ProductResponseDTO
     */
    @Override
    public Page<ProductResponseDTO> searchProductsByNamePaged(String name, Pageable pageable) {
        log.info("Searching paged products by name: {} with page: {}, size: {}",
                name, pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findByNameContaining(name, pageable);
        Page<ProductResponseDTO> result = productPage
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
        log.info("Found page {} of {} with {} products matching name: {}",
                result.getNumber(), result.getTotalPages(), result.getNumberOfElements(), name);
        return result;
    }

    /**
     * Retrieve products priced below a given value with pagination.
     *
     * @param price    the maximum price
     * @param pageable pagination information
     * @return a page of ProductResponseDTO
     */
    @Override
    public Page<ProductResponseDTO> getProductsUnderPricePaged(BigDecimal price, Pageable pageable) {
        log.info("Retrieving paged products under price: {} with page: {}, size: {}",
                price, pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findByPriceLessThan(price, pageable);
        Page<ProductResponseDTO> result = productPage
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
        log.info("Found page {} of {} with {} products under price: {}",
                result.getNumber(), result.getTotalPages(), result.getNumberOfElements(), price);
        return result;
    }

    /**
     * Retrieve all products that are currently in stock with pagination.
     *
     * @param pageable pagination information
     * @return a page of ProductResponseDTO
     */
    @Override
    public Page<ProductResponseDTO> getInStockProductsPaged(Pageable pageable) {
        log.info("Retrieving paged in-stock products with page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Product> productPage = productRepository.findByInStock(true, pageable);
        Page<ProductResponseDTO> result = productPage
                .map(product -> modelMapper.map(product, ProductResponseDTO.class));
        log.info("Found page {} of {} with {} in-stock products",
                result.getNumber(), result.getTotalPages(), result.getNumberOfElements());
        return result;
    }

    /**
     * Filter products using criteria with pagination.
     *
     * @param filter the filter criteria
     * @return a page of products matching the filter criteria
     */
    @Override
    public Page<ProductResponseDTO> filterProducts(ProductFilterDTO filter) {
        log.info("Filtering products with criteria: {}", filter);

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDirection(), filter.getSortBy())
        );

        // Apply specification and pagination
        Page<Product> productPage = productRepository.findAll(
                ProductSpecification.getProductSpecification(filter),
                pageable
        );

        // Map to DTOs
        Page<ProductResponseDTO> responsePage = productPage.map(
                product -> modelMapper.map(product, ProductResponseDTO.class)
        );

        log.info("Filtered {} products (page {} of {})",
                responsePage.getNumberOfElements(),
                responsePage.getNumber() + 1,
                responsePage.getTotalPages());

        return responsePage;
    }

    /**
     * Filter products using criteria with slice-based pagination.
     *
     * @param filter the filter criteria
     * @return a slice of products matching the filter criteria
     */
    @Override
    public Slice<ProductResponseDTO> filterProductsWithSlice(ProductFilterDTO filter) {
        log.info("Filtering products with criteria using slice: {}", filter);

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(filter.getSortDirection(), filter.getSortBy())
        );

        // Apply specification and pagination

        Slice<Product> productSlice = productRepository.findAll(
                Specification.where(ProductSpecification.getProductSpecification(filter)),
                pageable
        );

        // Map to DTOs
        Slice<ProductResponseDTO> responseSlice = productSlice.map(
                product -> modelMapper.map(product, ProductResponseDTO.class)
        );

        log.info("Filtered {} products (slice page {})",
                responseSlice.getNumberOfElements(),
                responseSlice.getNumber() + 1);

        return responseSlice;
    }


}
