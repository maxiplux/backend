package app.quantun.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
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
                description = "API for managing products in the system. This API provides endpoints for creating, updating, deleting, and retrieving products. It also includes endpoints for searching products by name, retrieving products under a specific price, and getting products that are in stock. Additionally, the API provides detailed error responses and examples for better understanding.",
                contact = @Contact(
                        name = "Your Company Name",
                        email = "support@yourcompany.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://springdoc.org"
                )
        ),
        servers = {
                @Server(
                        description = "Development server",
                        url = "http://localhost:8080",
                        variables = {
                                @ServerVariable(name = "port", defaultValue = "8080")
                        }
                ),
                @Server(
                        description = "Production server",
                        url = "https://api.yourcompany.com",
                        variables = {
                                @ServerVariable(name = "port", defaultValue = "443")
                        }
                )
        },
        tags = {
                @Tag(name = "Product Management", description = "Operations for managing products")
        }
)
public class OpenApiConfig {
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
}
