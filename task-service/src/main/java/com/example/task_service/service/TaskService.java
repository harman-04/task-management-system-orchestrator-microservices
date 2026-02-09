// Package declaration → groups related classes together.
package com.example.task_service.service;

import com.example.task_service.enums.TaskStatus; // Enum for task status (PENDING, ASSIGNED, DONE).
import com.example.task_service.taskModel.Task;  // Task entity mapped to MongoDB.

import java.util.List;

// TaskService → Interface that defines the contract for task-related operations.
// Interfaces only declare methods (no implementation).
// The actual logic will be provided in a class that implements this interface (e.g., TaskServiceImpl).
public interface TaskService {

    // ================================
    // Create Task
    // ================================
    // Purpose:
    // - Create a new task.
    // - requestRole → role of the requester (e.g., ADMIN, USER).
    // - Throws Exception if creation fails (e.g., invalid role).
    Task create(Task task, String requestRole) throws Exception;

    // ================================
    // Get Task by ID
    // ================================
    // Purpose:
    // - Fetch a task by its unique ID.
    // - Throws Exception if task not found.
    Task getTaskById(String id) throws Exception;

    // ================================
    // Get All Tasks
    // ================================
    // Purpose:
    // - Fetch all tasks with optional filters:
    //   - taskStatus → filter by status (PENDING, ASSIGNED, DONE).
    //   - sortByDeadline → sort tasks by deadline.
    //   - sortByCreatedAt → sort tasks by creation time.
    List<Task> getAllTasks(TaskStatus taskStatus, String sortByDeadline, String sortByCreatedAt);

    // ================================
    // Update Task
    // ================================
    // Purpose:
    // - Update an existing task by ID.
    // - updateTask → new task details.
    // - userId → ID of the user performing the update.
    // - Throws Exception if task not found or unauthorized.
    Task updateTask(String id, Task updateTask, String userId) throws Exception;

    // ================================
    // Delete Task
    // ================================
    // Purpose:
    // - Delete a task by its unique ID.
    // - Throws Exception if task not found.
    void deleteTask(String id) throws Exception;

    // ================================
    // Assign Task to User
    // ================================
    // Purpose:
    // - Assign a task to a specific user.
    // - id → task ID.
    // - userId → user ID.
    // - Throws Exception if task not found or assignment fails.
    Task assignedToUser(String id, String userId) throws Exception;

    // ================================
    // Get Tasks Assigned to User
    // ================================
    // Purpose:
    // - Fetch all tasks assigned to a specific user.
    // - Optional filters: taskStatus, sortByDeadline, sortByCreatedAt.
    List<Task> assignedUsersTask(String id, TaskStatus taskStatus, String sortByDeadline, String sortByCreatedAt);

    // ================================
    // Complete Task
    // ================================
    // Purpose:
    // - Mark a task as completed.
    // - taskId → ID of the task to complete.
    // - Throws Exception if task not found or already completed.
    Task completeTask(String taskId) throws Exception;
}