package app.quantun.backend.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j

public class GeneralConfig {
    @PostConstruct
    public void setHostnameProperty() {
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            log.info("Setting hostname to: {}", hostname);


            System.setProperty("HOSTNAME", hostname);
        } catch (UnknownHostException e) {
            log.error("Failed to set hostname {}", e.getMessage());
            System.setProperty("HOSTNAME", "unknown-host");
        }
    }
}
