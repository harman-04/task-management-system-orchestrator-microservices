// Package declaration → groups related classes together.
package com.example.task_service.controller;

import com.example.task_service.dto.UserDTO;              // DTO representing user details (from USER-SERVICE).
import com.example.task_service.enums.TaskStatus;         // Enum for task status (PENDING, ASSIGNED, DONE).
import com.example.task_service.service.TaskService;      // Service interface for task operations.
import com.example.task_service.service.UserServiceClient;// Feign client to call USER-SERVICE for user profile.
import com.example.task_service.taskModel.Task;           // Task entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;                    // Lombok → generates constructor for final fields.
import lombok.extern.slf4j.Slf4j;                         // Lombok → logging support.
import org.springframework.http.HttpStatus;               // HTTP status codes.
import org.springframework.http.ResponseEntity;           // Represents HTTP responses.
import org.springframework.web.bind.annotation.*;         // REST controller + mapping annotations.

import java.util.List;

// @RestController → Marks this class as a REST controller (returns JSON responses).
// @RequestMapping("/api/tasks") → Base URL for task-related endpoints.
// @RequiredArgsConstructor → Lombok generates constructor for final fields.
// @Slf4j → Enables logging with log.info(), log.warn(), log.error().
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Slf4j
public class TaskController {

    // ================================
    // Dependencies (Injected via Constructor)
    // ================================
    private final TaskService taskService;           // Provides task-related operations.
    private final UserServiceClient userServiceClient; // Feign client to call USER-SERVICE for user profile.

    // ================================
    // Create Task (POST /api/tasks)
    // ================================
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        log.info("Request to create task is received!");

        // Step 1: Fetch user profile from USER-SERVICE using JWT.
        UserDTO user = userServiceClient.getUserProfile(jwt);

        // Step 2: Only admins can create tasks.
        if (!"ROLE_ADMIN".equals(user.role())) {
            log.warn("Unauthorized task creation attempted by user with email: {}", user.email());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Step 3: Create task via TaskService.
        Task createdTask = taskService.create(task, user.role());

        // Step 4: Return created task with HTTP 201 Created.
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // ================================
    // Get Task by ID (GET /api/tasks/{id})
    // ================================
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id,
                                            @RequestHeader("Authorization") String jwt) throws Exception {
        if (jwt == null) {
            throw new Exception("JWT is needed to get task by id.");
        }

        Task task = taskService.getTaskById(id);
        return task != null ? ResponseEntity.ok(task) : ResponseEntity.notFound().build();
    }

    // ================================
    // Get Assigned User's Tasks (GET /api/tasks)
    // ================================
    @GetMapping
    public ResponseEntity<List<Task>> getAssignedUserTask(@RequestHeader("Authorization") String jwt,
                                                          @RequestParam(required = false) TaskStatus status,
                                                          @RequestParam(required = false) String sortByDeadline,
                                                          @RequestParam(required = false) String sortByCreatedAt) throws Exception {
        if (jwt == null) {
            throw new Exception("JWT is required for getting assigned user tasks");
        }

        // Step 1: Fetch user profile from USER-SERVICE.
        UserDTO user = userServiceClient.getUserProfile(jwt);

        // Step 2: Fetch tasks assigned to this user with optional filters.
        List<Task> tasks = taskService.assignedUsersTask(user.id(), status, sortByDeadline, sortByCreatedAt);

        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // ================================
    // Assign Task to User (PUT /api/tasks/{id}/user/{userId}/assigned)
    // ================================
    @PutMapping("/{id}/user/{userId}/assigned")
    public ResponseEntity<Task> assignedTaskToUser(@PathVariable String id,
                                                   @PathVariable String userId,
                                                   @RequestHeader("Authorization") String jwt) throws Exception {
        UserDTO user = userServiceClient.getUserProfile(jwt);
        Task task = taskService.assignedToUser(id, userId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    // ================================
    // Update Task (PUT /api/tasks/{id})
    // ================================
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id,
                                           @RequestBody Task req,
                                           @RequestHeader("Authorization") String jwt) throws Exception {
        if (jwt == null) {
            throw new Exception("JWT is required for updating the task.");
        }

        UserDTO user = userServiceClient.getUserProfile(jwt);
        Task task = taskService.updateTask(id, req, user.id());

        return task != null ? new ResponseEntity<>(task, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ================================
    // Delete Task (DELETE /api/tasks/{id})
    // ================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // ================================
    // Complete Task (PUT /api/tasks/{id}/complete)
    // ================================
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable String id) throws Exception {
        Task task = taskService.completeTask(id);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }
}