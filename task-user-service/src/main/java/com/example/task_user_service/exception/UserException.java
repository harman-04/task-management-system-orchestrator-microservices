// Package declaration → groups related classes together.
package com.example.task_user_service.exception;

// UserException → Custom exception class for handling user-related errors.
// Extends Exception → making it a checked exception (must be declared or handled).
public class UserException extends Exception {

    // ================================
    // Constructor
    // ================================
    // Accepts a custom error message when throwing the exception.
    // Example usage:
    //   throw new UserException("User not found with given email");
    //
    // The message is passed to the parent Exception class (super).
    public UserException(String message) {
        super(message);
    }
}