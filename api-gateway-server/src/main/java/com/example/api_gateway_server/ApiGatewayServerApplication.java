// Package declaration → groups related classes together.
package com.example.api_gateway_server;

import org.springframework.boot.SpringApplication;          // Utility class to bootstrap and launch Spring Boot application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration, component scanning, and configuration support.
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // Enables service discovery via Eureka.

// @SpringBootApplication → Combines three key annotations:
// 1. @Configuration → Marks this class as a source of bean definitions.
// 2. @EnableAutoConfiguration → Automatically configures Spring Boot based on dependencies.
// 3. @ComponentScan → Scans for Spring components (controllers, filters, etc.).
//
// @EnableDiscoveryClient → Registers this API Gateway with Eureka Server.
// - Allows the gateway to discover other microservices (USER-SERVICE, TASK-SERVICE, TASK-SUBMISSION-SERVICE).
// - Enables dynamic routing without hardcoding service URLs.
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayServerApplication {

    // ================================
    // Main Method
    // ================================
    // Purpose:
    // - Entry point of the API Gateway application.
    // - Calls SpringApplication.run() to bootstrap the app.
    // - Starts embedded server (Tomcat by default).
    // - Initializes Spring context and loads beans.
    // - Launches API Gateway on the configured port (e.g., 8080).
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayServerApplication.class, args);
    }
}