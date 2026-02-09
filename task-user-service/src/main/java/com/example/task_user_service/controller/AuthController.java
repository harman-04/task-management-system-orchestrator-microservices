// Package declaration → groups related classes together.
package com.example.task_user_service.controller;

import com.example.task_user_service.exception.UserException;          // Custom exception for user-related errors.
import com.example.task_user_service.repository.UserRepository;       // Repository for accessing User collection in MongoDB.
import com.example.task_user_service.request.LoginRequest;            // DTO for login requests (email + password).
import com.example.task_user_service.response.AuthResponse;           // Standardized response for authentication.
import com.example.task_user_service.service.CustomerServiceImplementation; // Loads user details for authentication.
import com.example.task_user_service.service.UserService;             // Service interface for user operations.
import com.example.task_user_service.taskSecurityConfig.JwtProvider;  // Utility for generating JWT tokens.
import com.example.task_user_service.usermodel.User;                  // User entity mapped to MongoDB.
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker; // Resilience4j annotation for fault tolerance.
import lombok.RequiredArgsConstructor;                                // Lombok → generates constructor for final fields.
import lombok.extern.slf4j.Slf4j;                                     // Lombok → logging support.
import org.springframework.http.HttpStatus;                           // HTTP status codes.
import org.springframework.http.ResponseEntity;                       // Represents HTTP responses.
import org.springframework.security.authentication.BadCredentialsException; // Exception for invalid login credentials.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Authentication object.
import org.springframework.security.core.Authentication;              // Represents authenticated user.
import org.springframework.security.core.context.SecurityContextHolder; // Holds authentication info for current request.
import org.springframework.security.core.userdetails.UserDetails;     // Represents user details for authentication.
import org.springframework.security.crypto.password.PasswordEncoder;  // Used for hashing and verifying passwords.
import org.springframework.web.bind.annotation.PostMapping;           // Maps HTTP POST requests.
import org.springframework.web.bind.annotation.RequestBody;           // Binds request body to method parameter.
import org.springframework.web.bind.annotation.RequestMapping;        // Maps base URL for controller.
import org.springframework.web.bind.annotation.RestController;        // Marks this class as a REST controller.

// @RestController → Marks this class as a REST controller (returns JSON responses).
// @RequestMapping("/auth") → Base URL for authentication endpoints.
// @RequiredArgsConstructor → Lombok generates constructor for final fields.
// @Slf4j → Enables logging with log.info(), log.error(), etc.
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    // ================================
    // Dependencies (Injected via Constructor)
    // ================================
    private final UserRepository userRepository; // For saving/fetching users from MongoDB.
    private final PasswordEncoder passwordEncoder; // For hashing and verifying passwords.
    private final CustomerServiceImplementation customerServiceImplementation; // Loads user details for login.
    private final UserService userService; // Provides user-related operations.

    // ================================
    // Signup Endpoint (POST /auth/signup)
    // ================================
    // Purpose:
    // - Registers a new user.
    // - Encodes password before saving.
    // - Generates JWT token after successful signup.
    @PostMapping("/signup")
    @CircuitBreaker(name = "userService", fallbackMethod = "signupFallback") // Resilience4j circuit breaker.
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {

        // Step 1: Check if email already exists.
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserException("Email is already used in another account.");
        }

        // Step 2: Encode password before saving.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        // Step 3: Log successful signup.
        log.info("User successfully signed up with name: {}", user.getFullName());

        // Step 4: Create Authentication object.
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 5: Return JWT token in AuthResponse.
        return ResponseEntity.ok(
                new AuthResponse(JwtProvider.generateToken(authentication), "Register Success", true)
        );
    }

    // ================================
    // Signin Endpoint (POST /auth/signin)
    // ================================
    // Purpose:
    // - Authenticates existing user.
    // - Validates credentials.
    // - Generates JWT token if login succeeds.
    @PostMapping("/signin")
    @CircuitBreaker(name = "userService", fallbackMethod = "signinFallback")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        // Step 1: Authenticate user.
        Authentication auth = authentication(loginRequest.getEmail(), loginRequest.getPassword());

        // Step 2: Store authentication in SecurityContext.
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Step 3: Log successful login.
        log.info("User successfully logged in with email: {}", loginRequest.getEmail());

        // Step 4: Return JWT token in AuthResponse.
        return ResponseEntity.ok(
                new AuthResponse(JwtProvider.generateToken(auth), "Login success", true)
        );
    }

    // ================================
    // Fallback Methods (Resilience4j)
    // ================================
    // Called when circuit breaker detects service failure.
    public ResponseEntity<AuthResponse> signupFallback(User user, Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new AuthResponse(null,
                        "Registration temporarily unavailable. AuthService is busy, try again later.",
                        false));
    }

    public ResponseEntity<AuthResponse> signinFallback(LoginRequest loginRequest, Throwable throwable) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new AuthResponse(null, "Login service is busy", false));
    }

    // ================================
    // Authentication Helper Method
    // ================================
    // Purpose:
    // - Validates email + password against stored user details.
    // - Throws BadCredentialsException if invalid.
    private Authentication authentication(String username, String password) {
        UserDetails userDetails = customerServiceImplementation.loadUserByUsername(username);

        if (userDetails == null || !passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Credential");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}