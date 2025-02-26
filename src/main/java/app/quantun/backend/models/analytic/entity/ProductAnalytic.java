package app.quantun.backend.models.analytic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a Product.
 * This class is mapped to the "Product" table in the database.
 */

@Data
@Table(name = "PRODUCT_ANALYTIC")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProductAnalytic {

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
