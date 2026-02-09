// Package declaration → groups related classes together.
package com.example.task_submission_service;

import org.springframework.boot.SpringApplication;          // Utility class to bootstrap and launch Spring Boot application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Enables auto-configuration, component scanning, and configuration support.
import org.springframework.cloud.openfeign.EnableFeignClients; // Enables Feign Client support for inter-service communication.

// @SpringBootApplication → Combines three key annotations:
// 1. @Configuration → Marks this class as a source of bean definitions.
// 2. @EnableAutoConfiguration → Automatically configures Spring Boot based on dependencies.
// 3. @ComponentScan → Scans for Spring components (controllers, services, repositories).
//
// @EnableFeignClients → Enables Feign Client functionality.
// - Allows this microservice to call other microservices (like USER-SERVICE and TASK-SERVICE) declaratively.
// - Feign automatically generates REST client implementations based on interfaces.
@SpringBootApplication
@EnableFeignClients
public class TaskSubmissionServiceApplication {

    // ================================
    // Main Method
    // ================================
    // Purpose:
    // - Entry point of the Task Submission Service application.
    // - Calls SpringApplication.run() to bootstrap the app.
    // - Starts embedded server (Tomcat by default).
    // - Initializes Spring context and loads beans.
    public static void main(String[] args) {
        SpringApplication.run(TaskSubmissionServiceApplication.class, args);
    }
}