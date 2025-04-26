package app.quantun.backend.models.contract.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Category requests.
 * This class is used to transfer category data between the client and the server.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    private Long id;

    /**
     * The name of the category.
     * It must not be blank and its length must not exceed 255 characters.
     */
    @NotBlank(message = "{category.name.required}")
    @Size(max = 255, message = "{category.name.maxLength}")
    private String name;

    /**
     * The description of the category.
     */
    private String description;
}