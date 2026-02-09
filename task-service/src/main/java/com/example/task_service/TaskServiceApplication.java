// Package declaration → groups related classes together.
package com.example.task_service;

import org.springframework.boot.SpringApplication;          // Utility class to bootstrap and launch Spring Boot application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration, component scanning, and configuration support.
import org.springframework.cloud.openfeign.EnableFeignClients; // Enables Feign Client support for inter-service communication.

// @SpringBootApplication → Combines three key annotations:
// 1. @Configuration → Marks this class as a source of bean definitions.
// 2. @EnableAutoConfiguration → Automatically configures Spring Boot based on dependencies.
// 3. @ComponentScan → Scans for Spring components (controllers, services, repositories).
//
// @EnableFeignClients → Enables Feign Client functionality.
// - Allows this microservice to call other microservices (like USER-SERVICE) declaratively.
// - Feign automatically generates REST client implementations based on interfaces.
@SpringBootApplication
@EnableFeignClients
public class TaskServiceApplication {

    // ================================
    // Main Method
    // ================================
    // Purpose:
    // - Entry point of the Task Service application.
    // - Calls SpringApplication.run() to bootstrap the app.
    // - Starts embedded server (Tomcat by default).
    // - Initializes Spring context and loads beans.
    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}