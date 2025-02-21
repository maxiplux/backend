package app.quantun.backend.models.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean inStock;
}

