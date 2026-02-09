This is the "Data Structural Design" chapter for the **Submission Microservice**. In this service, we see a shift from simple entity management to **Relationship Orchestration**. Even though we are using a NoSQL database, we are maintaining "Virtual Foreign Keys" to connect three separate microservices.

---

#  Microservice Pillar 1: Submission Domain & Cross-Service Data

The Submission Service is the bridge between the **User** who does the work and the **Task** that defines the work. It records the "proof of completion" (Github Link) and manages the approval state.

---

##  1. The TaskSubmission Model (`TaskSubmission.java`)

This document is the "Receipt" of a finished task.

### **Internal Structure & Design Choices:**

* **Virtual Foreign Keys (`taskId`, `userId`)**:
* **Recall Note:** In Microservices, we never use `@OneToOne` or `@ManyToOne` across different databases. Instead, we store the IDs as simple Strings. This is called **Data Sovereignty**. The Submission Service "owns" the submission, but it "references" the user and task.


* **The "Payload" (`githubLink`)**: This is the core data. It represents the actual work submitted by the user.
* **Automatic Defaulting**: Notice that `status` defaults to `PENDING`. This enforces the business rule that work must be reviewed by an Admin before it is considered "Done."

---

##  2. `SubmissionStatus.java`: The Approval Pipeline

This enum defines the "Governance" of your task management system.

* **PENDING**: Work is in the queue.
* **ACCEPTED**: The "Happy Path." Once an admin sets this, the task lifecycle ends successfully.
* **REJECTED**: The "Correction Loop." The user likely needs to fix their code and resubmit.

> **Interview Tip:** If asked how you handle state transitions, explain that using an **Enum** prevents the database from ever having a status like "Almost Done" or "Fixing," which ensures data consistency across the whole cluster.

---

##  3. The Dual DTO Strategy (`UserDTO` & `TaskDTO`)

Since this service sits at the center, it needs two "Windows" into other services.

* **`UserDTO`**: Used to verify if the submitter is a valid user and to display their name during Admin review.
* **`TaskDTO`**: Used to verify if the task exists and is currently in the `ASSIGNED` state before allowing a user to submit a link.
* **Java Records**: We continue using **Records** here because they are thread-safe and perfect for "Read-Only" data coming from external API calls.

---

##  4. Infrastructure & Database Isolation

Your `application.yaml` reflects the strict isolation required for high-scale systems.

* **Dedicated Database (`/taskSubmissionService`)**: Even though you are using the same MongoDB Atlas cluster, this service writes to its own logical database. This ensures that a bug in the Task Service cannot corrupt the Submission data.
* **Tracing (`Zipkin`)**: Because this service triggers calls to both User and Task services, your Zipkin graph will show a "Triple-Hop" trace. This is where you can identify if the network latency is happening in the User Service or the Task Service.

---

##  Implementation Summary Table

| Component | Logic Purpose | "Key Takeaway" for Recall |
| --- | --- | --- |
| **`taskId` / `userId`** | Identity Linking | Replaces SQL Joins with ID-based lookups in the Service layer. |
| **`SubmissionStatus`** | Workflow Control | Ensures a standard lifecycle (Pending -> Accepted/Rejected). |
| **`githubLink`** | Proof of Work | The primary value delivered by the "Customer" role. |
| **`SubRepository`** | Persistence | Handles the storage of "Submission Receipts" in MongoDB. |

---

### **Recall Checklist for your Notes:**

1. **Loose Coupling**: We store IDs, not Objects.
2. **State Control**: Enums prevent illegal workflow steps.
3. **Data Mapping**: DTOs allow us to "see" into other services without owning their data.

---

This part of the **Submission Service** is where the "Microservices Orchestration" happens. It doesn't just manage its own data; it coordinates actions between the **User Service** and the **Task Service** to complete a business transaction.

---

# Microservice Pillar 2: Multi-Service Coordination & Implementation

In this layer, we use **Feign Clients** as communication bridges and implement logic that spans across service boundaries.

---

##  1. Dual Feign Orchestration

This service is a "Client" to two different "Servers."

### **The User Check (`UserServiceClient`)**

* **Role:** Fetches the profile to ensure the user exists before processing a submission.
* **Recall Note:** This ensures that we aren't accepting submissions from "ghost" users who aren't in the system.

### **The Task Check & Update (`TaskServiceClient`)**

This is more complex. It handles two specific lifecycle events:

1. **Validation (`getTaskById`)**: Before a user can submit work, we check the Task Service to ensure the task actually exists.
2. **Lifecycle Completion (`completeTask`)**: This is a **cross-service state update**. When an admin accepts a submission here, this service tells the Task Service: *"Hey, this task is officially done, update your status!"*

---

##  2.`SubmissionServiceImplementation`: The "Judge" Logic

This is where the business rules for work approval are enforced.

### **The Submission Workflow (`submitTask`)**

1. **The Remote Fetch**: It calls `taskServiceClient.getTaskById`.
2. **The Security Context**: It passes the `jwt` header. This is **Credential Forwarding**. Without this, the Task Service would reject the request as unauthorized.
3. **The Creation**: If the task is valid, it records the `githubLink` and timestamps it.

### **The Approval Logic (`acceptDeclineTaskSubmission`)**

This is a high-level coordination method.

* **Status Mapping**: It converts a String from the controller into a Type-Safe `SubmissionStatus` Enum.
* **Side Effects**: If the status is `ACCEPTED`, it triggers a remote call to the Task Service.
* **Theoretical Note:** In a fully optimized system, this cross-service update might be done using **Kafka Events** to ensure "Eventual Consistency" if the Task Service is temporarily down.



---

##  3. Stream-Based Filtering

Just like the Task Service, we use Java Streams to filter submissions by `taskId`.

* **Concept:** Instead of creating complex MongoDB queries, we fetch all and filter in memory.
* **Trade-off:** This is fine for small/medium datasets. For millions of submissions, you would want a custom `@Query` in your repository to let the database do the work.

---

##  Implementation Summary Table

| Logic Step | File / Method | Collaborative Action |
| --- | --- | --- |
| **Identity Verification** | `UserServiceClient` | Verifies the submitter's identity. |
| **Task Validation** | `TaskServiceClient` | Ensures the task is open for submission. |
| **Proof of Work** | `submitTask()` | Saves the code link and sets status to `PENDING`. |
| **Final Approval** | `acceptDeclineTaskSubmission()` | Updates local status AND tells Task Service to mark task as `DONE`. |

---

### **Recall Checklist for your Notes:**

1. **Orchestration**: This service "orchestrates" a flow that involves 3 separate databases (User, Task, Submission).
2. **Propagating Identity**: Always pass the `jwt` in Feign calls if the downstream service is protected.
3. **State Sync**: If you accept a submission here, you must update the task status in the other service to keep the system in sync.

---

This is the final chapter of your **Submission Microservice** documentation. It describes the "Workflow Orchestration" layer, where HTTP requests trigger a complex sequence of events involving multiple services to finalize a task's lifecycle.

---

#  Microservice Pillar 3: API Gateways & Workflow Orchestration

The `SubController` acts as the command center for the "Submission-to-Approval" pipeline. It is responsible for gathering data from headers, parameters, and external services to execute business transactions.

---

##  1. The Submission Lifecycle API (`SubController`)

The controller coordinates the three-way interaction between the User, the Task, and the Submission record.

### **The Submission Flow (`POST /api/submissions`)**

When a user submits their work:

1. **Identity Verification**: The controller extracts the JWT from the `@RequestHeader`. It calls the **User Service** to resolve the `UserDTO`.
2. **Contextual Submission**: It takes the `taskId` and `githubLink` as `@RequestParam`.
3. **Persistence**: The logic then moves to the service layer to create a `PENDING` record.

### **The Approval Flow (`PUT /api/submissions/{id}`)**

This is where the system closes the loop. An admin provides the submission ID and a status (`ACCEPTED` or `REJECTED`).

* **Triggering Side Effects**: If `ACCEPTED` is passed, the internal service logic triggers a Feign call to the **Task Service** to mark the original task as `DONE`.
* **Result**: This ensures that data remains consistent across different databases.

---

##  2. Request Handling Patterns

This service demonstrates the three primary ways to send data to a Spring Boot REST API:

* **`@PathVariable`**: Used for identifying specific resources (e.g., `/{id}`).
* **`@RequestParam`**: Used for simple inputs like a URL (`githubLink`) or a status toggle.
* **`@RequestHeader`**: Used exclusively for security context (`Authorization`).

---

##  3. The "Big Picture" Infrastructure (2026 Context)

With the completion of this third service, your microservice ecosystem now looks like this:

1. **User Service**: Manages accounts and provides the "Passport" (JWT).
2. **Task Service**: Manages the "What" (the work to be done).
3. **Submission Service**: Manages the "How" (the proof of work and approval).

### **Why this 3-Service Split?**

* **Scalability**: If you have 1,000 users but 1,000,000 submissions, you can scale the **Submission Service** instances independently without wasting resources on the User Service.
* **Fault Isolation**: If the Submission database goes down, users can still log in and view their tasks, keeping the "Critical Path" of your app alive.

---

##  Implementation Summary Table

| API Endpoint | Method | Key Actor | Business Outcome |
| --- | --- | --- | --- |
| **`/api/submissions`** | `POST` | User | Submits work (GitHub Link) for a task. |
| **`/api/submissions/{id}`** | `PUT` | Admin | Approves/Rejects work; updates Task Service status. |
| **`/task/{taskId}`** | `GET` | Admin/User | Views all submission attempts for a specific task. |
| **`/submissions`** | `GET` | System | Health check endpoint for monitoring. |

---

### **Recall Checklist for the Submission Service**

1. **Orchestration**: This service is a "Client" to two others. It must handle the JWT propagation carefully.
2. **State Sync**: Always remember that `ACCEPTED` here triggers an update in the `TASK-SERVICE`.
3. **Logging**: Use the home controller as a "Heartbeat" to verify the service is registered correctly in Eureka.

---

### **What's Next?**

Your core business services are ready. However, they are currently running on separate ports (`8081`, `8082`, `8083`). For a frontend (React) to use them easily, you need a **Single Entry Point**.

