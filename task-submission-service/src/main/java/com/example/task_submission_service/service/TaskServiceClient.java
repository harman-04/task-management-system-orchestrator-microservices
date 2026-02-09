// Package declaration → groups related classes together.
package com.example.task_submission_service.service;

import com.example.task_submission_service.dto.TaskDTO;       // Data Transfer Object (DTO) representing task details.
import org.springframework.cloud.openfeign.FeignClient;      // Feign → declarative REST client for inter-service communication.
import org.springframework.web.bind.annotation.GetMapping;   // Maps HTTP GET requests.
import org.springframework.web.bind.annotation.PathVariable; // Binds path variables in URL.
import org.springframework.web.bind.annotation.PutMapping;   // Maps HTTP PUT requests.
import org.springframework.web.bind.annotation.RequestHeader;// Binds request headers (like Authorization JWT).

// @FeignClient(name = "TASK-SERVICE")
// Purpose:
// - Declares this interface as a Feign client.
// - "TASK-SERVICE" → the name of the microservice registered in Eureka.
// - Feign will automatically generate an implementation to call TASK-SERVICE endpoints.
@FeignClient(name = "TASK-SERVICE")
public interface TaskServiceClient {

    // ================================
    // Get Task by ID (via TASK-SERVICE)
    // ================================
    // Purpose:
    // - Calls TASK-SERVICE endpoint: GET /api/tasks/{id}
    // - Requires Authorization header (JWT token).
    // - Returns TaskDTO (task details).
    //
    // Example:
    //   taskServiceClient.getTaskById("task123", "Bearer <jwt_token>");
    //   → fetches task details from TASK-SERVICE.
    @GetMapping("/api/tasks/{id}")
    TaskDTO getTaskById(@PathVariable String id, @RequestHeader("Authorization") String jwt);

    // ================================
    // Complete Task (via TASK-SERVICE)
    // ================================
    // Purpose:
    // - Calls TASK-SERVICE endpoint: PUT /api/tasks/{id}/complete
    // - Marks the task as completed (status = DONE).
    // - Returns updated TaskDTO.
    //
    // Example:
    //   taskServiceClient.completeTask("task123");
    //   → marks task with id "task123" as completed in TASK-SERVICE.
    @PutMapping("/api/tasks/{id}/complete")
    TaskDTO completeTask(@PathVariable String id);
}