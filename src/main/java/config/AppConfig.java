package config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {"controllers", "services", "repositories", "models"})
@EnableJpaRepositories(basePackages = "repositories")
@EntityScan(basePackages = "models")
public class AppConfig {
}
