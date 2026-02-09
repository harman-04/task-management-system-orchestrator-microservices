// Package declaration → groups related classes together.
package com.example.task_service.dto;

// UserDTO → A Java record used as a Data Transfer Object (DTO).
// Purpose:
// - Represents user details in a simplified form.
// - Used when communicating between microservices (e.g., via Feign Client).
// - Prevents exposing sensitive fields like password.
// - Immutable → once created, values cannot be changed.
public record UserDTO(
        String id,        // Unique identifier of the user (MongoDB ObjectId).
        String fullName,  // Full name of the user.
        String email,     // Email address (used for login and communication).
        String role,      // Role of the user (e.g., ROLE_ADMIN, ROLE_CUSTOMER).
        String mobile     // Mobile number of the user.
) { }