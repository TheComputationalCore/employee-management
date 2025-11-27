package com.example.employeemanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI employeeManagementOpenAPI() {

        Server local = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Server production = new Server()
                .url("https://employee-management-production.up.railway.app")
                .description("Production Server");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Employee Management System API")
                                .version("1.0.0")
                                .description("Comprehensive REST API for the Employee Management Web Application.")
                                .contact(new Contact()
                                        .name("Dinesh — TheComputationalCore")
                                        .email("support@thecomputationalcore.com")
                                        .url("https://github.com/TheComputationalCore")
                                )
                                .license(new License()
                                        .name("MIT License")
                                        .url("https://opensource.org/licenses/MIT")
                                )
                )
                .servers(List.of(local, production));
    }
}
