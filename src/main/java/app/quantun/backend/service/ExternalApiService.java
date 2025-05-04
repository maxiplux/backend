package app.quantun.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Service for interacting with external APIs using Spring's RestClient.
 * This class demonstrates proper usage of RestClient for external API calls.
 */
@Service
@Slf4j
public class ExternalApiService {

    private final RestClient restClient;

    /**
     * Creates a new ExternalApiService with a configured RestClient.
     * 
     * @param restClientBuilder the RestClient.Builder to use
     */
    public ExternalApiService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .baseUrl("https://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> {
                log.error("Client error: {} {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("API client error: " + response.getStatusCode());
            })
            .build();
    }

    /**
     * Fetches a product from the external API by its ID.
     * 
     * @param productId the ID of the product to fetch
     * @return the product data
     */
    public ProductDto getProduct(Long productId) {
        log.info("Fetching product with ID: {}", productId);
        return restClient.get()
            .uri("/products/{id}", productId)
            .retrieve()
            .body(ProductDto.class);
    }

    /**
     * Searches for products in the external API.
     * 
     * @param query the search query
     * @return a list of matching products
     */
    public List<ProductDto> searchProducts(String query) {
        log.info("Searching products with query: {}", query);
        return restClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/products/search")
                .queryParam("q", query)
                .build())
            .retrieve()
            .body(new ParameterizedTypeReference<List<ProductDto>>() {});
    }

    /**
     * Creates a new product in the external API.
     * 
     * @param request the product creation request
     * @return the created product
     */
    public ProductDto createProduct(ProductCreationRequest request) {
        log.info("Creating new product: {}", request.getName());
        return restClient.post()
            .uri("/products")
            .body(request)
            .retrieve()
            .body(ProductDto.class);
    }

    /**
     * DTO class for product data from external API.
     */
    public static class ProductDto {
        private Long id;
        private String name;
        private String description;
        private Double price;

        // Getters and setters
    }

    /**
     * Request class for creating a product in the external API.
     */
    public static class ProductCreationRequest {
        private String name;
        private String description;
        private Double price;

        public String getName() {
            return name;
        }

        // Other getters and setters
    }
}
