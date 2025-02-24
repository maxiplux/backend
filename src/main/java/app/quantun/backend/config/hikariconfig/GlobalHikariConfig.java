package app.quantun.backend.config.hikariconfig;

import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties
@Slf4j

public class GlobalHikariConfig {


    @Primary
    @Bean(name = "primaryHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public HikariConfig primaryHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "analyticHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.analytic")
    public HikariConfig analyticHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "snowFlakeHikariConfig")
    @ConfigurationProperties(prefix = "spring.datasource.snowflake")
    public HikariConfig snowFlakeHikariConfig() {
        return new HikariConfig();
    }

}
