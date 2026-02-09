// Package declaration → groups related classes together.
package com.example.task_service.service;

import com.example.task_service.dto.UserDTO;          // Data Transfer Object (DTO) representing user details.
import org.springframework.cloud.openfeign.FeignClient; // Feign → declarative REST client for inter-service communication.
import org.springframework.web.bind.annotation.GetMapping; // Maps HTTP GET requests.
import org.springframework.web.bind.annotation.RequestHeader; // Binds request headers (like Authorization JWT).

// @FeignClient(name = "USER-SERVICE")
// Purpose:
// - Declares this interface as a Feign client.
// - "USER-SERVICE" → the name of the microservice registered in Eureka.
// - Feign will automatically generate an implementation to call USER-SERVICE endpoints.
@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    // ================================
    // Get User Profile (via USER-SERVICE)
    // ================================
    // Purpose:
    // - Calls the USER-SERVICE endpoint: GET /api/users/profile
    // - Requires Authorization header (JWT token).
    // - Returns UserDTO (user details without exposing sensitive info).
    //
    // Example:
    //   userServiceClient.getUserProfile("Bearer <jwt_token>");
    //   → fetches logged-in user's profile from USER-SERVICE.
    @GetMapping("api/users/profile")
    public UserDTO getUserProfile(@RequestHeader("Authorization") String jwt);
}