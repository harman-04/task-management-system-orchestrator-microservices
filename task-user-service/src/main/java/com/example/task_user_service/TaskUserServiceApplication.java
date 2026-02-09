// Package declaration → groups related classes together.
package com.example.task_user_service;

import org.springframework.boot.SpringApplication;          // Utility class to bootstrap and launch Spring Boot application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Annotation to enable auto-configuration, component scanning, and configuration support.

// @SpringBootApplication → Combines three key annotations:
// 1. @Configuration → Marks this class as a source of bean definitions.
// 2. @EnableAutoConfiguration → Automatically configures Spring Boot based on dependencies.
// 3. @ComponentScan → Scans for Spring components (controllers, services, repositories) in the package.
//
// This annotation makes the class the main entry point for the Spring Boot application.
@SpringBootApplication
public class TaskUserServiceApplication {

    // ================================
    // Main Method
    // ================================
    // Purpose:
    // - Entry point of the application.
    // - Calls SpringApplication.run() to bootstrap the app.
    // - Starts embedded server (Tomcat by default).
    // - Initializes Spring context and loads beans.
    public static void main(String[] args) {
        SpringApplication.run(TaskUserServiceApplication.class, args);
    }
}