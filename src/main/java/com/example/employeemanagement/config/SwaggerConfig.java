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
                        .title("Employee Management System API")
                        .description("""
                                This API powers the Employee Management System.
                                Features include CRUD operations, search, pagination,
                                and enterprise-grade validation.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dinesh Chandra — TheComputationalCore")
                                .url("https://github.com/TheComputationalCore")
                                .email("support@thecomputationalcore.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://your-railway-deploy-url").description("Production Server")
                ));
    }

}
