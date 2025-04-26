package app.quantun.backend.models.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category responses.
 * This class is used to transfer category data between the server and the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    /**
     * The ID of the category.
     */
    private Long id;

    /**
     * The name of the category.
     */
    private String name;

    /**
     * The description of the category.
     */
    private String description;
}