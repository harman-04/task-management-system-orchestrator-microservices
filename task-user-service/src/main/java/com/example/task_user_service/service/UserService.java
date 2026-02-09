// Package declaration → groups related classes together.
package com.example.task_user_service.service;

import com.example.task_user_service.exception.UserException; // Custom exception for user-related errors.
import com.example.task_user_service.usermodel.User;          // User entity mapped to MongoDB.

import java.util.List; // Used for returning lists of users.

// UserService → Interface that defines the contract for user-related operations.
// Interfaces in Java only declare methods (no implementation).
// The actual logic will be provided in a class that implements this interface (e.g., UserServiceImpl).
public interface UserService {

    // ================================
    // Get All Users
    // ================================
    // Purpose:
    // - Fetch all users from the database.
    // - Throws UserException if something goes wrong (e.g., DB error).
    public List<User> getAllUsers() throws UserException;

    // ================================
    // Find User Profile by JWT
    // ================================
    // Purpose:
    // - Extract user information from JWT token.
    // - Useful for "profile" endpoints where logged-in user details are needed.
    // - Throws UserException if token is invalid or user not found.
    public User findUserProfileByJwt(String jwt) throws UserException;

    // ================================
    // Find User by Email
    // ================================
    // Purpose:
    // - Fetch user by email address.
    // - Commonly used during login or account lookup.
    // - Throws UserException if user not found.
    public User findUserByEmail(String email) throws UserException;

    // ================================
    // Find User by ID
    // ================================
    // Purpose:
    // - Fetch user by unique MongoDB ID.
    // - Throws UserException if user not found.
    public User findUserById(String userId) throws UserException;

    // ================================
    // Find All Users (Duplicate)
    // ================================
    // Purpose:
    // - Another method to fetch all users.
    // - Unlike getAllUsers(), this one does not throw UserException.
    // - Could be used in contexts where exception handling is not required.
    public List<User> findAllUsers();
}