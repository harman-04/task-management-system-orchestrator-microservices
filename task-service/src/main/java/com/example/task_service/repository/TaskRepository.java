// Package declaration → groups related classes together.
package com.example.task_service.repository;

import com.example.task_service.taskModel.Task;          // Task entity mapped to MongoDB.
import org.springframework.data.mongodb.repository.MongoRepository; // Spring Data MongoDB repository base interface.

import java.util.List;

// TaskRepository → Interface for performing CRUD operations on Task collection in MongoDB.
// Extends MongoRepository<Task, String>:
// - Task → entity type.
// - String → type of the primary key (id).
public interface TaskRepository extends MongoRepository<Task, String> {

    // ================================
    // Custom Query Method: Find Tasks by Assigned User
    // ================================
    // Purpose:
    // - Fetch all tasks assigned to a specific user.
    // - Uses Spring Data MongoDB query derivation (method name → query).
    // Example:
    //   taskRepository.findByAssignedUserId("user123")
    //   → returns all tasks where assignedUserId = "user123".
    public List<Task> findByAssignedUserId(String userId);

    // ================================
    // Custom Query Method: Delete Task by ID
    // ================================
    // Purpose:
    // - Deletes a task by its unique ID.
    // - Overrides default deleteById() for clarity.
    // Example:
    //   taskRepository.deleteById("63f1a2b4c9e77a1234567890")
    //   → deletes the task with that ID from MongoDB.
    public void deleteById(String id);
}