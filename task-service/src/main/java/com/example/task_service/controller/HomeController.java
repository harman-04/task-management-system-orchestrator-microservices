// Package declaration → groups related classes together.
package com.example.task_service.controller;

import org.springframework.http.ResponseEntity;   // Represents HTTP responses with status + body.
import org.springframework.web.bind.annotation.GetMapping; // Maps HTTP GET requests to controller methods.
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller.

// @RestController → Marks this class as a Spring MVC REST controller.
// - Combines @Controller + @ResponseBody.
// - All methods return JSON or plain text responses by default.
@RestController
public class HomeController {

    // ================================
    // Home Endpoint (GET /tasks)
    // ================================
    // Purpose:
    // - Provides a simple welcome message for the Task Service.
    // - Useful for health checks or basic service verification.
    // Response:
    // - Returns plain text "Welcome to task service".
    @GetMapping("/tasks")
    public ResponseEntity<String> homeController() {
        // ResponseEntity.ok() → returns HTTP 200 OK with body.
        return ResponseEntity.ok("Welcome to task service");
    }
}