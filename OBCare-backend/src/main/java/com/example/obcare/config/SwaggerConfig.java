package com.example.obcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.util.List;
import java.util.Collections;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
            .info(new Info().title("OBCare Backend API")
            .version("1.0"))
            .servers(List.of(new Server().url("http://localhost:8082")));
}
}
