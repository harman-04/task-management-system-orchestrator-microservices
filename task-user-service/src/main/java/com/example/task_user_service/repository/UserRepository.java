// Package declaration → groups related classes together.
package com.example.task_user_service.repository;

import com.example.task_user_service.usermodel.User; // Import the User model (MongoDB document).
import org.springframework.data.mongodb.repository.MongoRepository; // Spring Data MongoDB repository interface.
import org.springframework.data.mongodb.repository.Query;            // Annotation for custom MongoDB queries.

// UserRepository → Interface for MongoDB operations on User collection.
// Extends MongoRepository<User, String>:
// - User → The entity type (MongoDB document).
// - String → The type of the primary key (@Id field in User).
//
// Spring Data MongoDB automatically provides CRUD methods like:
// - save(User user)
// - findById(String id)
// - findAll()
// - deleteById(String id)
//
// You can also define custom query methods.
public interface UserRepository extends MongoRepository<User, String> {

    // ================================
    // Custom Query: Find User by Email
    // ================================
    // @Query("{email:?0}")
    // - Defines a MongoDB query using JSON syntax.
    // - {email:?0} → Matches documents where "email" field equals the first method parameter (?0).
    // - ?0 → Placeholder for the first argument passed to the method.
    //
    // Example:
    //   userRepository.findByEmail("harmandeep@example.com");
    //   → Executes MongoDB query: { "email" : "harmandeep@example.com" }
    //
    // Returns:
    // - User object if found.
    // - null if no user exists with that email.
    @Query("{email:?0}")
    User findByEmail(String email);
}