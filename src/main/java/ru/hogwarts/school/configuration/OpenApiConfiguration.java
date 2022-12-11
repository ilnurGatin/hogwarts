package ru.hogwarts.school.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI schoolApi() {
        return new OpenAPI()
                .info(new Info()
                    .title("School API")
                    .description("API to manage Hogwarts").version("0.0.1"));
    }
}
