package com.thecomputationalcore.employeemanagement.config;

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

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Server railwayServer = new Server()
                .url("https://your-railway-url.up.railway.app")
                .description("Railway Production Server");

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management System API")
                        .description("REST API for Employee Management Web Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dinesh Chandra — TheComputationalCore")
                                .email("support@thecomputationalcore.com")
                                .url("https://github.com/TheComputationalCore"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                .servers(List.of(localServer, railwayServer));
    }
}
