package app.quantun.backend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The main class for the Backend Application.
 * This class contains the main method which serves as the entry point for the Spring Boot application.
 */
@SpringBootApplication
@EnableCaching
public class BackendApplication {


    /**
     * The main method which serves as the entry point for the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {

        SpringApplication.run(BackendApplication.class, args);
    }

}
