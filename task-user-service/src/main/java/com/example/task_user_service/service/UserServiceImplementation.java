// Package declaration → groups related classes together.
package com.example.task_user_service.service;

import com.example.task_user_service.exception.UserException;   // Custom exception for user-related errors.
import com.example.task_user_service.repository.UserRepository; // Repository for accessing User collection in MongoDB.
import com.example.task_user_service.taskSecurityConfig.JwtProvider; // Utility for extracting email from JWT tokens.
import com.example.task_user_service.usermodel.User;           // User entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;                         // Lombok → generates constructor for final fields.
import org.springframework.stereotype.Service;                 // Marks this class as a Spring-managed service bean.

import java.util.List;
import java.util.Optional;

// @Service → Marks this class as a service component.
// @RequiredArgsConstructor → Lombok generates constructor for final fields (dependency injection).
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    // ================================
    // Dependency Injection
    // ================================
    // Injects UserRepository to interact with MongoDB.
    private final UserRepository userRepository;

    // ================================
    // Get All Users
    // ================================
    // Delegates to findAllUsers().
    // Throws UserException if needed (though here it just calls findAllUsers).
    @Override
    public List<User> getAllUsers() throws UserException {
        return findAllUsers();
    }

    // ================================
    // Find User Profile by JWT
    // ================================
    // Purpose:
    // - Extract email from JWT using JwtProvider.
    // - Fetch user from MongoDB by email.
    // - Throw UserException if user not found.
    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        // Step 1: Extract email from JWT.
        String email = JwtProvider.getEmailFromJwtToken(jwt);

        // Step 2: Find user by email.
        User user = userRepository.findByEmail(email);

        // Step 3: If user not found → throw exception.
        if (user == null) {
            throw new UserException("User not found with email extracted from jwt: " + email);
        }
        return user;
    }

    // ================================
    // Find User by Email
    // ================================
    // Purpose:
    // - Fetch user by email directly.
    // - Throws UserException if user not found.
    @Override
    public User findUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email);
    }

    // ================================
    // Find User by ID
    // ================================
    // Purpose:
    // - Fetch user by MongoDB ID.
    // - Uses Optional to handle null values.
    // - Throws UserException if user not found.
    @Override
    public User findUserById(String userId) throws UserException {
        Optional<User> opt = userRepository.findById(userId);

        if (opt.isEmpty()) {
            throw new UserException("User not found with id " + userId);
        }
        return opt.get();
    }

    // ================================
    // Find All Users
    // ================================
    // Purpose:
    // - Fetch all users from MongoDB.
    // - Returns list of users.
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}