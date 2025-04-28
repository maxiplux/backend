package app.quantun.backend.config;

import app.quantun.backend.exception.DataInitializationException;
import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.models.contract.response.CategoryResponseDTO;
import app.quantun.backend.models.entity.Category;
import app.quantun.backend.models.entity.Product;
import app.quantun.backend.repository.CategoryRepository;
import app.quantun.backend.repository.ProductRepository;
import app.quantun.backend.service.CategoryService;
import app.quantun.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration class for initializing sample data for the application.
 * This class creates sample categories and products when the application starts.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private static final String DATA_VERSION = "1.0"; // For tracking data initialization versions
    
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Initialize sample data for the application.
     * This method creates sample categories and products.
     *
     * @return a CommandLineRunner that initializes the data
     */
    @Bean
    @Profile("!test") // Don't run in test profile
    public CommandLineRunner initData() {
        return args -> {
            try {
                log.info("Starting data initialization with version {}", DATA_VERSION);

                // Check if data already exists
                if (categoryRepository.count() > 0 || productRepository.count() > 0) {
                    log.info("Data already exists, skipping initialization");
                    return;
                }

                // Create categories
                Map<String, CategoryResponseDTO> categories = createCategories();

                // Create products
                createProducts(categories);

                log.info("Data initialization completed successfully");
            } catch (Exception e) {
                log.error("Error during data initialization: {}", e.getMessage(), e);
                throw new DataInitializationException("Failed to initialize sample data", e);
            }
        };
    }

    /**
     * Create sample categories.
     *
     * @return a map of category names to CategoryResponseDTO objects
     */
    private Map<String, CategoryResponseDTO> createCategories() {
        log.info("Creating sample categories...");

        Map<String, CategoryResponseDTO> categories = new HashMap<>();
        List<CategoryRequestDTO> categoryRequests = List.of(
            buildCategoryRequest("Electronics", "Electronic devices and accessories"),
            buildCategoryRequest("Clothing", "Apparel and fashion items"),
            buildCategoryRequest("Books", "Books, e-books, and publications"),
            buildCategoryRequest("Home & Kitchen", "Home appliances and kitchen essentials")
        );

        for (CategoryRequestDTO request : categoryRequests) {
            try {
                CategoryResponseDTO response = categoryService.createCategory(request);
                categories.put(request.getName(), response);
                log.debug("Created category: {}", request.getName());
            } catch (Exception e) {
                log.error("Failed to create category {}: {}", request.getName(), e.getMessage());
                throw new DataInitializationException("Failed to create category: " + request.getName(), e);
            }
        }

        log.info("Created {} categories", categories.size());
        return categories;
    }

    /**
     * Helper method to build a CategoryRequestDTO.
     */
    private CategoryRequestDTO buildCategoryRequest(String name, String description) {
        return CategoryRequestDTO.builder()
                .name(name)
                .description(description)
                .build();
    }

    /**
     * Create sample products and associate them with categories.
     *
     * @param categories a map of category names to CategoryResponseDTO objects
     */
    private void createProducts(Map<String, CategoryResponseDTO> categories) {
        log.info("Creating sample products...");

        createElectronicsProducts(getCategoryId(categories, "Electronics"));
        createClothingProducts(getCategoryId(categories, "Clothing"));
        createBooksProducts(getCategoryId(categories, "Books"));
        createHomeKitchenProducts(getCategoryId(categories, "Home & Kitchen"));

        log.info("Created products for all categories");
    }

    /**
     * Helper method to get category ID with error handling.
     */
    private Long getCategoryId(Map<String, CategoryResponseDTO> categories, String categoryName) {
        return Optional.ofNullable(categories.get(categoryName))
                .map(CategoryResponseDTO::getId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryName));
    }

    /**
     * Create sample electronics products.
     *
     * @param categoryId the ID of the Electronics category
     */
    private void createElectronicsProducts(Long categoryId) {
        Category category = getCategoryById(categoryId);

        List<ProductData> products = List.of(
            new ProductData("Smartphone X", "Latest smartphone with advanced features", "999.99", 50),
            new ProductData("Laptop Pro", "High-performance laptop for professionals", "1499.99", 30),
            new ProductData("Wireless Headphones", "Noise-cancelling wireless headphones", "199.99", 100)
        );

        saveProducts(products, category);
    }

    /**
     * Create sample clothing products.
     *
     * @param categoryId the ID of the Clothing category
     */
    private void createClothingProducts(Long categoryId) {
        Category category = getCategoryById(categoryId);

        List<ProductData> products = List.of(
            new ProductData("Cotton T-Shirt", "Comfortable cotton t-shirt in various colors", "19.99", 200),
            new ProductData("Slim Fit Jeans", "Classic slim fit jeans for everyday wear", "49.99", 150),
            new ProductData("Winter Jacket", "Warm winter jacket with water-resistant exterior", "129.99", 75)
        );

        saveProducts(products, category);
    }

    /**
     * Create sample books products.
     *
     * @param categoryId the ID of the Books category
     */
    private void createBooksProducts(Long categoryId) {
        Category category = getCategoryById(categoryId);

        List<ProductData> products = List.of(
            new ProductData("Bestselling Novel", "Award-winning fiction novel by renowned author", "14.99", 300),
            new ProductData("Gourmet Cookbook", "Collection of gourmet recipes from around the world", "29.99", 120),
            new ProductData("Computer Science Textbook", "Comprehensive guide to computer science principles", "79.99", 0, false)
        );

        saveProducts(products, category);
    }

    /**
     * Create sample home & kitchen products.
     *
     * @param categoryId the ID of the Home & Kitchen category
     */
    private void createHomeKitchenProducts(Long categoryId) {
        Category category = getCategoryById(categoryId);

        List<ProductData> products = List.of(
            new ProductData("High-Speed Blender", "Powerful blender for smoothies and food preparation", "89.99", 60),
            new ProductData("Programmable Coffee Maker", "Automatic coffee maker with timer and multiple settings", "59.99", 45),
            new ProductData("4-Slice Toaster", "Stainless steel toaster with multiple browning settings", "39.99", 80)
        );

        saveProducts(products, category);
    }

    /**
     * Get category by ID with improved error handling.
     */
    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + categoryId));
    }

    /**
     * Helper method to save products from ProductData objects.
     */
    private void saveProducts(List<ProductData> productsData, Category category) {
        for (ProductData data : productsData) {
            try {
                Product product = new Product();
                product.setName(data.name);
                product.setDescription(data.description);
                product.setPrice(new BigDecimal(data.price));
                product.setInStock(data.inStock);
                product.setStock(data.stock);
                product.setCategory(category);
                productRepository.save(product);
                log.debug("Created product: {} in category: {}", data.name, category.getName());
            } catch (Exception e) {
                log.error("Failed to create product {}: {}", data.name, e.getMessage());
                throw new DataInitializationException("Failed to create product: " + data.name, e);
            }
        }
    }

    /**
     * Data class for product information.
     */
    private static class ProductData {
        private final String name;
        private final String description;
        private final String price;
        private final int stock;
        private final boolean inStock;

        public ProductData(String name, String description, String price, int stock) {
            this(name, description, price, stock, stock > 0);
        }

        public ProductData(String name, String description, String price, int stock, boolean inStock) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.inStock = inStock;
        }
    }
}
