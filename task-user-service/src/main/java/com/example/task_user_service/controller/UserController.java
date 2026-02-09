// Package declaration → groups related classes together.
package com.example.task_user_service.controller;

import com.example.task_user_service.exception.UserException;   // Custom exception for user-related errors.
import com.example.task_user_service.service.UserService;      // Service interface for user operations.
import com.example.task_user_service.usermodel.User;           // User entity mapped to MongoDB.
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // Resilience4j circuit breaker for fault tolerance.
import lombok.RequiredArgsConstructor;                         // Lombok → generates constructor for final fields.
import lombok.extern.slf4j.Slf4j;                              // Lombok → logging support.
import org.springframework.http.HttpStatus;                    // HTTP status codes.
import org.springframework.http.ResponseEntity;                // Represents HTTP responses.
import org.springframework.web.bind.annotation.*;              // REST controller + mapping annotations.

import java.util.List;

// @RestController → Marks this class as a REST controller (returns JSON responses).
// @RequestMapping("/api/users") → Base URL for user-related endpoints.
// @RequiredArgsConstructor → Lombok generates constructor for final fields.
// @Slf4j → Enables logging with log.info(), log.error(), etc.
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    // ================================
    // Dependency Injection
    // ================================
    private final UserService userService; // Provides user-related operations.

    // ================================
    // Get User Profile (GET /api/users/profile)
    // ================================
    // Purpose:
    // - Fetch logged-in user's profile using JWT token.
    // - Removes password before returning response.
    @GetMapping("/profile")
    @CircuitBreaker(name = "userService", fallbackMethod = "profileFallback")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws UserException {
        log.info("Fetching profile using JWT Token");
        User user = userService.findUserProfileByJwt(jwt);
        user.setPassword(null); // Hide password in response.
        return ResponseEntity.ok(user);
    }

    // ================================
    // Find User by ID (GET /api/users/{userId})
    // ================================
    // Purpose:
    // - Fetch user by MongoDB ID.
    // - Removes password before returning response.
    @GetMapping("/{userId}")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackForFindUserById")
    public ResponseEntity<User> findUserById(@PathVariable String userId,
                                             @RequestHeader("Authorization") String jwt) throws UserException {
        log.info("Searching user for id: {}", userId);
        User user = userService.findUserById(userId);
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    // ================================
    // Find All Users (GET /api/users/all)
    // ================================
    // Purpose:
    // - Fetch all registered users.
    // - Removes passwords before returning response.
    @GetMapping("/all")
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackForFindAllUsers")
    public ResponseEntity<List<User>> findAllUsers() {
        log.info("Fetching all registered users");
        List<User> users = userService.findAllUsers();
        users.forEach(u -> u.setPassword(null)); // Hide passwords.
        return new ResponseEntity<>(users, HttpStatus.ACCEPTED);
    }

    // ================================
    // Fallback Methods (Resilience4j)
    // ================================
    // Called when circuit breaker detects service failure.
    public ResponseEntity<User> profileFallback(String jwt, Throwable throwable) {
        log.error("Circuit Breaker trigger for profile: {}", throwable.getMessage());
        return ResponseEntity.internalServerError().build();
    }

    public ResponseEntity<User> fallbackForFindUserById(String userId, String jwt, Throwable throwable) {
        log.error("Circuit Breaker trigger for findUserById: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    public ResponseEntity<List<User>> fallbackForFindAllUsers(String jwt, Throwable throwable) {
        log.error("Circuit Breaker trigger for findAllUsers: {}", throwable.getMessage());
        return ResponseEntity.ok(List.of()); // Return empty list if service unavailable.
    }

    // ================================
    // Get All Users (GET /api/users)
    // ================================
    // Purpose:
    // - Another endpoint to fetch all users.
    // - Handles exceptions manually instead of circuit breaker.
    @GetMapping()
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String jwt) {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error retrieving users: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error retrieving users");
        }
    }
}