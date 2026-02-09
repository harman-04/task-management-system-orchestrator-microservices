// Package declaration → groups related classes together.
package com.example.task_submission_service.repository;

import com.example.task_submission_service.submissionModel.TaskSubmission; // TaskSubmission entity mapped to MongoDB.
import org.springframework.data.mongodb.repository.MongoRepository;       // Spring Data MongoDB repository base interface.

// SubRepository → Interface for performing CRUD operations on TaskSubmission collection in MongoDB.
// Extends MongoRepository<TaskSubmission, String>:
// - TaskSubmission → entity type.
// - String → type of the primary key (id).
//
// By extending MongoRepository, this interface automatically inherits common CRUD methods:
// - save(TaskSubmission entity)
// - findById(String id)
// - findAll()
// - deleteById(String id)
// - existsById(String id)
// - count()
//
// No need to write boilerplate queries — Spring Data generates them automatically.
public interface SubRepository extends MongoRepository<TaskSubmission, String> {
    // Currently, no custom query methods are defined.
    // You can add custom queries here if needed, for example:
    // List<TaskSubmission> findByUserId(String userId);
    // List<TaskSubmission> findByTaskId(String taskId);
}