package app.quantun.backend;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ServletInitializer class for configuring the Spring Boot application.
 * This class extends SpringBootServletInitializer and overrides the configure method.
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * Configure the application.
     *
     * @param application the SpringApplicationBuilder
     * @return the configured SpringApplicationBuilder
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BackendApplication.class);
    }

}
