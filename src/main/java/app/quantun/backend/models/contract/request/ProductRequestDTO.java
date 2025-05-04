package app.quantun.backend.models.contract.request;

import app.quantun.backend.models.contract.validator.ValidBase64FileSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Product requests.
 * This class is used to transfer product data between the client and the server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {


    /**
     * The name of the product.
     * It must not be blank and its length must not exceed 255 characters.
     */
    @NotBlank(message = "{product.name.required}")
    @Size(max = 255, message = "{product.name.maxLength}")
    private String name;

    /**
     * The description of the product.
     * It must not be blank.
     */
    @NotBlank(message = "{product.description.required}")
    private String description;

    /**
     * The price of the product.
     * It must be a positive value.
     */
    @Positive(message = "{product.price.positive}")
    private BigDecimal price;

    /**
     * Indicates whether the product is in stock.
     */
    private boolean inStock;

    /**
     * The quantity of the product in stock.
     * It must be a non-negative value.
     */
    @PositiveOrZero(message = "{product.stock.positive}")
    private int stock;

    @ValidBase64FileSize(maxSizeBytes = 5 * 1024 * 1024, message = "File size must not exceed 1MB")
    private String base64File;
}
