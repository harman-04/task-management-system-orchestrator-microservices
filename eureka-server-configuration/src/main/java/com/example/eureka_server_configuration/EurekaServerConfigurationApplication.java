// Package declaration → groups related classes together.
package com.example.eureka_server_configuration;

import org.springframework.boot.SpringApplication;          // Utility class to bootstrap and launch Spring Boot application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration, component scanning, and configuration support.
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer; // Enables Eureka Server functionality.

// @SpringBootApplication → Combines three key annotations:
// 1. @Configuration → Marks this class as a source of bean definitions.
// 2. @EnableAutoConfiguration → Automatically configures Spring Boot based on dependencies.
// 3. @ComponentScan → Scans for Spring components (controllers, services, repositories).
//
// @EnableEurekaServer → Turns this application into a Eureka Server.
// - Eureka Server acts as a "service registry" in microservices architecture.
// - Other microservices (USER-SERVICE, TASK-SERVICE, TASK-SUBMISSION-SERVICE) register themselves here.
// - Enables service discovery → microservices can find and communicate with each other without hardcoding URLs.
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerConfigurationApplication {

    // ================================
    // Main Method
    // ================================
    // Purpose:
    // - Entry point of the Eureka Server application.
    // - Calls SpringApplication.run() to bootstrap the app.
    // - Starts embedded server (Tomcat by default).
    // - Initializes Spring context and loads beans.
    // - Launches Eureka Server on the configured port (e.g., 8085).
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerConfigurationApplication.class, args);
    }
}