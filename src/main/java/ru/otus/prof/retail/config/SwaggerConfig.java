package ru.otus.prof.retail.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI retailChainOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Retail Chain API")
                        .description("API для управления сетью магазинов")
                        .version("1.0"));
    }
}