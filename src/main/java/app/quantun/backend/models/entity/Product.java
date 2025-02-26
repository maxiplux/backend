package app.quantun.backend.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a Product.
 * This class is mapped to the "Product" table in the database.
 */

@Entity
@Table(name = "PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product.
     * It must not be blank.
     */
    @NotBlank(message = "Product name is required")
    private String name;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The price of the product.
     * It must be a positive value.
     */
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    /**
     * Indicates whether the product is in stock.
     */
    private boolean inStock;

    /**
     * The quantity of the product in stock.
     */
    private int stock;

    /**
     * The date and time when the product was created.
     */
    @CreatedDate
    private LocalDateTime createdDate;

    /**
     * The date and time when the product was last modified.
     */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
