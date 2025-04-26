package app.quantun.backend.config;

import app.quantun.backend.models.contract.request.CategoryRequestDTO;
import app.quantun.backend.models.contract.request.ProductRequestDTO;
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
import java.util.Map;

/**
 * Configuration class for initializing sample data for the application.
 * This class creates sample categories and products when the application starts.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

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
            log.info("Starting data initialization...");
            
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
        
        // Electronics category
        CategoryRequestDTO electronics = CategoryRequestDTO.builder()
                .name("Electronics")
                .description("Electronic devices and accessories")
                .build();
        categories.put("Electronics", categoryService.createCategory(electronics));
        
        // Clothing category
        CategoryRequestDTO clothing = CategoryRequestDTO.builder()
                .name("Clothing")
                .description("Apparel and fashion items")
                .build();
        categories.put("Clothing", categoryService.createCategory(clothing));
        
        // Books category
        CategoryRequestDTO books = CategoryRequestDTO.builder()
                .name("Books")
                .description("Books, e-books, and publications")
                .build();
        categories.put("Books", categoryService.createCategory(books));
        
        // Home & Kitchen category
        CategoryRequestDTO homeKitchen = CategoryRequestDTO.builder()
                .name("Home & Kitchen")
                .description("Home appliances and kitchen essentials")
                .build();
        categories.put("Home & Kitchen", categoryService.createCategory(homeKitchen));
        
        log.info("Created {} categories", categories.size());
        return categories;
    }
    
    /**
     * Create sample products and associate them with categories.
     *
     * @param categories a map of category names to CategoryResponseDTO objects
     */
    private void createProducts(Map<String, CategoryResponseDTO> categories) {
        log.info("Creating sample products...");
        
        // Since ProductRequestDTO doesn't have a category field, we need to create the Product entities directly
        // and set their category before saving them
        
        // Electronics products
        createElectronicsProducts(categories.get("Electronics").getId());
        
        // Clothing products
        createClothingProducts(categories.get("Clothing").getId());
        
        // Books products
        createBooksProducts(categories.get("Books").getId());
        
        // Home & Kitchen products
        createHomeKitchenProducts(categories.get("Home & Kitchen").getId());
        
        log.info("Created products for all categories");
    }
    
    /**
     * Create sample electronics products.
     *
     * @param categoryId the ID of the Electronics category
     */
    private void createElectronicsProducts(Long categoryId) {
        // Find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create products
        Product smartphone = new Product();
        smartphone.setName("Smartphone X");
        smartphone.setDescription("Latest smartphone with advanced features");
        smartphone.setPrice(new BigDecimal("999.99"));
        smartphone.setInStock(true);
        smartphone.setStock(50);
        smartphone.setCategory(category);
        productRepository.save(smartphone);
        
        Product laptop = new Product();
        laptop.setName("Laptop Pro");
        laptop.setDescription("High-performance laptop for professionals");
        laptop.setPrice(new BigDecimal("1499.99"));
        laptop.setInStock(true);
        laptop.setStock(30);
        laptop.setCategory(category);
        productRepository.save(laptop);
        
        Product headphones = new Product();
        headphones.setName("Wireless Headphones");
        headphones.setDescription("Noise-cancelling wireless headphones");
        headphones.setPrice(new BigDecimal("199.99"));
        headphones.setInStock(true);
        headphones.setStock(100);
        headphones.setCategory(category);
        productRepository.save(headphones);
    }
    
    /**
     * Create sample clothing products.
     *
     * @param categoryId the ID of the Clothing category
     */
    private void createClothingProducts(Long categoryId) {
        // Find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create products
        Product tShirt = new Product();
        tShirt.setName("Cotton T-Shirt");
        tShirt.setDescription("Comfortable cotton t-shirt in various colors");
        tShirt.setPrice(new BigDecimal("19.99"));
        tShirt.setInStock(true);
        tShirt.setStock(200);
        tShirt.setCategory(category);
        productRepository.save(tShirt);
        
        Product jeans = new Product();
        jeans.setName("Slim Fit Jeans");
        jeans.setDescription("Classic slim fit jeans for everyday wear");
        jeans.setPrice(new BigDecimal("49.99"));
        jeans.setInStock(true);
        jeans.setStock(150);
        jeans.setCategory(category);
        productRepository.save(jeans);
        
        Product jacket = new Product();
        jacket.setName("Winter Jacket");
        jacket.setDescription("Warm winter jacket with water-resistant exterior");
        jacket.setPrice(new BigDecimal("129.99"));
        jacket.setInStock(true);
        jacket.setStock(75);
        jacket.setCategory(category);
        productRepository.save(jacket);
    }
    
    /**
     * Create sample books products.
     *
     * @param categoryId the ID of the Books category
     */
    private void createBooksProducts(Long categoryId) {
        // Find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create products
        Product novel = new Product();
        novel.setName("Bestselling Novel");
        novel.setDescription("Award-winning fiction novel by renowned author");
        novel.setPrice(new BigDecimal("14.99"));
        novel.setInStock(true);
        novel.setStock(300);
        novel.setCategory(category);
        productRepository.save(novel);
        
        Product cookbook = new Product();
        cookbook.setName("Gourmet Cookbook");
        cookbook.setDescription("Collection of gourmet recipes from around the world");
        cookbook.setPrice(new BigDecimal("29.99"));
        cookbook.setInStock(true);
        cookbook.setStock(120);
        cookbook.setCategory(category);
        productRepository.save(cookbook);
        
        Product textbook = new Product();
        textbook.setName("Computer Science Textbook");
        textbook.setDescription("Comprehensive guide to computer science principles");
        textbook.setPrice(new BigDecimal("79.99"));
        textbook.setInStock(false);
        textbook.setStock(0);
        textbook.setCategory(category);
        productRepository.save(textbook);
    }
    
    /**
     * Create sample home & kitchen products.
     *
     * @param categoryId the ID of the Home & Kitchen category
     */
    private void createHomeKitchenProducts(Long categoryId) {
        // Find the category
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create products
        Product blender = new Product();
        blender.setName("High-Speed Blender");
        blender.setDescription("Powerful blender for smoothies and food preparation");
        blender.setPrice(new BigDecimal("89.99"));
        blender.setInStock(true);
        blender.setStock(60);
        blender.setCategory(category);
        productRepository.save(blender);
        
        Product coffeemaker = new Product();
        coffeemaker.setName("Programmable Coffee Maker");
        coffeemaker.setDescription("Automatic coffee maker with timer and multiple settings");
        coffeemaker.setPrice(new BigDecimal("59.99"));
        coffeemaker.setInStock(true);
        coffeemaker.setStock(45);
        coffeemaker.setCategory(category);
        productRepository.save(coffeemaker);
        
        Product toaster = new Product();
        toaster.setName("4-Slice Toaster");
        toaster.setDescription("Stainless steel toaster with multiple browning settings");
        toaster.setPrice(new BigDecimal("39.99"));
        toaster.setInStock(true);
        toaster.setStock(80);
        toaster.setCategory(category);
        productRepository.save(toaster);
    }
}