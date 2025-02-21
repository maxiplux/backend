package app.quantun.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * This class provides a bean for ModelMapper to be used throughout the application.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Creates and returns a ModelMapper bean.
     *
     * @return a ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
