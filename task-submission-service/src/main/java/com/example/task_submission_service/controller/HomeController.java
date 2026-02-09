// Package declaration → groups related classes together.
package com.example.task_submission_service.controller;

import org.springframework.http.HttpStatus;        // Represents HTTP status codes (200 OK, 404 Not Found, etc.).
import org.springframework.http.ResponseEntity;  // Represents HTTP responses with status + body.
import org.springframework.web.bind.annotation.GetMapping;   // Maps HTTP GET requests to controller methods.
import org.springframework.web.bind.annotation.RestController; // Marks this class as a REST controller.

// @RestController → Marks this class as a Spring MVC REST controller.
// - Combines @Controller + @ResponseBody.
// - All methods return JSON or plain text responses by default.
@RestController
public class HomeController {

    // ================================
    // Home Endpoint (GET /submissions)
    // ================================
    // Purpose:
    // - Provides a simple welcome message for the Task Submission Service.
    // - Useful for health checks or basic service verification.
    // Response:
    // - Returns plain text "Welcome to Task SubmissionService".
    // - HTTP Status: 200 OK.
    @GetMapping("/submissions")
    public ResponseEntity<String> homeController() {
        // ResponseEntity<String> → wraps response body + HTTP status.
        return new ResponseEntity<>("Welcome to Task SubmissionService", HttpStatus.OK);
    }
}