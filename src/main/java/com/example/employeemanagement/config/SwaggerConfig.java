package com.example.employeemanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management API")
                        .version("1.0.0")
                        .description("Premium REST API documentation for Employee Management System")
                        .contact(new Contact()
                                .name("Dinesh — TheComputationalCore")
                                .email("support@thecomputationalcore.com")
                                .url("https://github.com/TheComputationalCore"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}
