// Package declaration → groups related classes together.
package com.example.task_user_service.controller;

import com.example.task_user_service.response.ApiResponse; // Standardized API response wrapper.
import org.springframework.http.ResponseEntity;            // Represents HTTP responses with status + body.
import org.springframework.web.bind.annotation.GetMapping; // Maps HTTP GET requests to controller methods.
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller.

// @RestController → Marks this class as a Spring MVC REST controller.
// - Combines @Controller + @ResponseBody.
// - All methods return JSON responses by default.
@RestController
public class HomeController {

    // ================================
    // Root Endpoint ("/")
    // ================================
    // Purpose:
    // - Provides a welcome message for the microservice.
    // - Useful for health checks or basic info.
    // Response:
    // - Returns ApiResponse with message + status.
    @GetMapping("/")
    public ResponseEntity<ApiResponse> home() {
        return ResponseEntity.ok(
                new ApiResponse("Welcome to the Task Management MicroService.", true)
        );
    }

    // ================================
    // User Endpoint ("/users")
    // ================================
    // Purpose:
    // - Provides a welcome message specific to the User Service.
    // - Can be used to verify that user-related APIs are accessible.
    // Response:
    // - Returns ApiResponse with message + status.
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> userHome() {
        return ResponseEntity.ok(
                new ApiResponse("Welcome to the Task Management User Service", true)
        );
    }
}