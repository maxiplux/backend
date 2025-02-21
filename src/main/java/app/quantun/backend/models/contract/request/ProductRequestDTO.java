package app.quantun.backend.models.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "{product.name.required}")
    @Size(max = 255, message = "{product.name.maxLength}")


    private String name;

    private String description;

    @Positive(message = "{product.price.positive}")

    private BigDecimal price;

    private boolean inStock;
}
