This is the **technical blueprint** for the Task Service. It focuses on how data is modeled in a NoSQL environment and how we represent "Foreign" data in a microservices world.

---

#  Microservice Pillar 1: Task Domain & Infrastructure

This service handles the core business logic of the application. It is designed to be **loosely coupled** from the User Service while still maintaining data integrity.

---

##  1. The Task Model (`Task.java`)

In a microservices architecture, we use **Domain-Driven Design (DDD)**. The Task model only contains information relevant to a "Task."

### **Key Technical Features:**

* **Loose Coupling (`assignedUserId`)**: We do not store a `User` object here. We only store a `String assignedUserId`.
* **Recall Note:** This prevents a "dependency hell." The Task service doesn't need to know the User's password or mobile number to store a task. It only needs the ID to link them later via an API call.


* **JSON Serialization (`@JsonFormat`)**: We force the `TaskStatus` enum to be sent as a **String** (e.g., `"PENDING"`) rather than its numeric index. This makes the API much easier for a Frontend developer to read.
* **Temporal Tracking**: We track `createdAt` and `deadline` using `LocalDateTime`. In a real-world app, this allows us to send "Overdue" notifications or sort tasks by urgency.

---

##  2. `TaskStatus.java`: State Machine Basics

The `TaskStatus` enum defines the legal "Life Cycle" of a task.

* **PENDING**: Created but not yet given to a person.
* **ASSIGNED**: Currently being worked on by a user.
* **DONE**: Completed and verified.

> **Recall Note:** Using an Enum ensures that no one can accidentally set a task status to "In Progress" or "Deleted" if it's not part of the defined business rules.

---

##  3. `UserDTO.java`: Representing External Data

This is a **Java Record** (introduced in Java 14+). It is used to hold data coming *from* the User Service.

* **Why a Record?** Records are immutable. Since this is data we are just "borrowing" from the User Service to show on a UI, we don't want to accidentally change it. It has no boilerplate (no getters/setters needed).
* **The Blueprint**: It mirrors only the fields we need from the User Service (id, name, email, role, mobile).

---

##  4. `TaskRepository.java`: The Query Layer

Extending `MongoRepository` gives us automatic CRUD, but we added a custom query:

* **`findByAssignedUserId(String userId)`**: This allows us to quickly fetch a "My Tasks" list for any specific user.
* **Performance Note**: In a production MongoDB, you would want to create an **Index** on `assignedUserId` to keep this query fast as your database grows to thousands of tasks.

---

##  5. `application.yaml`: Service Identity

This file registers the service into the "Service Mesh."

* **Service Discovery**: It registers as `TASK-SERVICE` on port `8082`.
* **Database Isolation**: Notice the MongoDB URI points to a specific database: `/taskService`. Even though you might share the same MongoDB Cluster, the **User Service** and **Task Service** have their own isolated databases. This is the **Database-per-Service** pattern.
* **Observability**: Just like the User Service, it sends 100% of its traces to Zipkin at `localhost:9411`.

---

##  Implementation Summary Table

| Component | Logic Purpose | Why it matters? |
| --- | --- | --- |
| **`Task.java`** | Core Data Model | Defines exactly what a "Task" is in our system. |
| **`UserDTO.java`** | External Data Holder | Allows us to display User info without owning User data. |
| **`TaskRepository`** | Custom Data Retrieval | Enables the "Get Tasks by User" feature. |
| **`application.yaml`** | Service Discovery/Tracing | Allows the service to be found by Eureka and tracked by Zipkin. |

---

This part of the **Task Service** represents the "Business Engine" and the "Communication Bridge." It contains the logic for managing task lifecycles and the mechanism for talking to other microservices.

---

# ️ Microservice Pillar 2: Business Logic & Inter-Service Communication

In a distributed system, services must be smart enough to manage their own data but humble enough to ask other services for information they don't own.

---

## ️ 1. `TaskServiceImplementation`: The Business Engine

This class handles the "CRUD+" logic. It doesn't just save data; it enforces business rules (e.g., security checks and state transitions).

### **Key Logical Concepts:**

* **Role-Based Access Control (RBAC)**: In the `create` method, we manually check if the `requestRole` is `ROLE_ADMIN`. This ensures that even if a request hits the service, the business logic provides a second layer of defense.
* **Stream-Based Filtering**: We use **Java Streams** to filter tasks by status. This is more flexible than writing ten different repository methods.
* **Temporal Sorting**: The `assignedUsersTask` method uses `Comparator.comparing` with `nullsLast`.
* **Recall Note:** This prevents the application from crashing if a task is missing a deadline while you are trying to sort the list.


* **State Management**: Methods like `assignedToUser` and `completeTask` handle the transition between `PENDING` -> `ASSIGNED` -> `DONE`.

---

##  2. `UserServiceClient`: The OpenFeign Bridge

This is the most critical "Microservices" component. It allows the Task Service to interact with the User Service.

### **How it Works Internally:**

* **Declarative Client**: By using `@FeignClient(name="USER-SERVICE")`, we don't need to know the IP address or Port of the User Service. OpenFeign looks up "USER-SERVICE" in the **Eureka Registry** automatically.
* **Token Propagation**: Notice the `@RequestHeader("Authorization") String jwt`.
* **Concept:** When a user calls the Task Service, the Task Service "forwards" that same JWT to the User Service. This is called **Token Relaying**. It ensures the User Service knows exactly who is asking for the profile.



---

##  3. Functional Interface Pattern

We use the **Interface-Implementation pattern** (`TaskService` interface + `TaskServiceImplementation` class).

* **Loose Coupling**: If we want to change how tasks are assigned (e.g., using an AI algorithm), we can create a new implementation class without changing a single line of code in the `TaskController`.
* **Testing**: This makes it easy to "Mock" the service layer when writing unit tests for the controllers.

---

##  Implementation Summary Table

| Logic Component | File/Method | Theoretical "Why?" |
| --- | --- | --- |
| **Inter-Service Communication** | `UserServiceClient` | Enables "Join" logic across different databases/services using Feign. |
| **Access Guard** | `create()` | Enforces the rule: "Only Admins can create tasks." |
| **Data Flow** | `completeTask()` | Finalizes the task lifecycle and persists the "DONE" status. |
| **Dynamic Sorting** | `assignedUsersTask()` | Uses Java Streams to provide a customized view for the end-user. |

---

### **Recall Checklist for your Notes:**

1. **Feign** = No hardcoded URLs (Uses Eureka).
2. **Streams** = Clean data filtering in memory.
3. **Token Propagation** = Passing the JWT from one service to the next to maintain security.

---
This is the final chapter of the **Task Service** documentation. It covers the "Entry Points" (Controllers) where external requests are received and the crucial role of **Inter-Service Communication** during the request lifecycle.

---

#  Microservice Pillar 3: API Entry Points & Inter-Service Flow

This layer acts as the coordinator. It receives HTTP requests from the client, identifies the user by communicating with the **User Service**, and executes the business logic.

---

##  1. `TaskController.java`: The Service Coordinator

The controller does more than just route requests; it acts as a "Guard" using data fetched from across the network.

### **The Inter-Service Check Pattern**

In methods like `createTask`, the Task Service doesn't have the user's role in its own database.

1. **The Request**: A user sends a `POST` request with a JWT.
2. **The "Call-Back"**: The controller uses `userServiceClient.getUserProfile(jwt)` to ask the **User Service**: *"Who does this token belong to and what is their role?"*
3. **The Guard**: It checks `user.role()`. If it isn't `ROLE_ADMIN`, it returns `403 Forbidden` before the logic even starts.

---

## 🔗 2. Token Relaying (Security Propagation)

Notice that almost every `@GetMapping` or `@PostMapping` method accepts `@RequestHeader("Authorization") String jwt`.

* **Concept:** This is called **Security Context Propagation**.
* **Recall Note:** Because the microservices are stateless, the Task Service must "pass the baton" (the JWT) to the User Service so that the User Service can authorize the request. Without passing this header, the Feign call would fail with a `401 Unauthorized`.

---

##  3. Handling Dynamic Queries (`@RequestParam`)

The `getAssignedUserTask` method demonstrates how to handle optional filtering and sorting in a RESTful way.

* **Optional Parameters**: By using `(required = false)`, the API remains flexible. A user can request `/api/tasks` (all tasks) or `/api/tasks?status=DONE&sortByDeadline=true` (filtered and sorted).
* **The Logic Chain**: The Controller passes these parameters to the `TaskServiceImplementation`, which uses **Java Streams** to process the data in memory.

---

##  4. The `@EnableFeignClients` Annotation

This is the "On Switch" for inter-service communication.

* **Context**: Usually placed on the main `Application` class or a Configuration class.
* **Mechanism**: It tells Spring to scan for interfaces marked with `@FeignClient` and generate the underlying implementation that knows how to use **Eureka** to find other services.

---

##  Implementation Summary Table

| API Endpoint | Logic Responsibility | Security Rule |
| --- | --- | --- |
| **`POST /api/tasks`** | Create new task | **Strict:** Only `ROLE_ADMIN` can trigger this. |
| **`GET /api/tasks`** | Fetch user-specific tasks | Uses Feign to get User ID from the JWT. |
| **`PUT /{id}/assigned`** | Task Assignment | Links a Task ID to a User ID in MongoDB. |
| **`DELETE /{id}`** | Resource Cleanup | Returns `204 No Content` on successful deletion. |
| **`PUT /{id}/complete`** | State Transition | Moves the task status to `DONE`. |

---

### **Recall Checklist for the Task Service**

1. **Feign Client** is the bridge. If the User Service is down, these controllers will throw an error (this is why we use **Circuit Breakers**).
2. **Records (UserDTO)** are used for the response from Feign because they are lightweight and immutable.
3. **Logging (`@Slf4j`)** is used to track "Unauthorized" attempts, which is critical for security auditing.

---

### **Summary of the System So Far**

You now have two services that talk to each other:

* **User Service**: Owns the users and generates the "Passport" (JWT).
* **Task Service**: Owns the tasks and "Validates" the passport by calling the User Service.

