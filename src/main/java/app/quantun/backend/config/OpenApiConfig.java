package app.quantun.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI.
 * This class provides the OpenAPI definition for the application.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Management API",
                version = "1.0.0",
                description = "API for managing products in the system",
                contact = @Contact(
                        name = "Your Company Name",
                        email = "support@yourcompany.com"
                )
        )
)
public class OpenApiConfig {
}
