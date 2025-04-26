package app.quantun.backend.models.contract.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {

    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    // @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be positive")
    private Long categoryId;

    @PositiveOrZero(message = "Minimum price must be positive or zero")
    private BigDecimal minPrice;

    @PositiveOrZero(message = "Maximum price must be positive or zero")
    private BigDecimal maxPrice;

    private Boolean inStock;

    @PositiveOrZero(message = "Minimum stock must be positive or zero")
    private Integer minStock;

    // Date range filters
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private LocalDateTime updatedAfter;
    private LocalDateTime updatedBefore;

    // Pagination parameters
    @Min(value = 0, message = "Page number must be positive or zero")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 10;

    // Sorting parameters
    @Size(max = 50, message = "Sort field name must be less than 50 characters")
    private String sortBy = "id";

    private Sort.Direction sortDirection = Sort.Direction.ASC;

    @AssertTrue(message = "At least one filter field must be provided")
    public boolean isAtLeastOneFieldPresent() {
        // you can use @Notnull to force a specific item to be present
        //// @NotNull(message = "Category ID is required")
        return name != null || minPrice != null || maxPrice != null ||
                categoryId != null || description != null;
    }
}
