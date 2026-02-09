
---

#  Microservice Pillar 1: Stateless Security & JWT Infrastructure

This section covers the configuration that protects the entire microservice ecosystem. It utilizes **Spring Security 6.x** and **JJWT 0.12+**.

---

##  1. `ApplicationConfig.java`: The Security Blueprint

This file defines the **Security Filter Chain**. Think of this as the "Customs Office" of your application.

### **Core Theoretical Concepts:**

* **Statelessness (`SessionCreationPolicy.STATELESS`)**: In microservices, we don't use `HttpSession`. The server doesn't store any user state. If the server restarts, users stay logged in because the state is in the **token**, not the server's memory.
* **Filter Ordering (`addFilterBefore`)**: We inject `JwtTokenValidator` before `BasicAuthenticationFilter`. This is critical. We want to check for a "Passport" (JWT) before Spring tries to ask for "Username/Password" (Basic Auth).
* **CORS (Cross-Origin Resource Sharing)**: Browsers block scripts from one domain (localhost:3000) from calling another (localhost:8081) for security. This configuration explicitly "whitelists" your frontend and exposes the `Authorization` header so the frontend can save the token.

---

##  2. `JwtProvider.java`: The Token Mint

This utility class is responsible for **creating (Signing)** and **reading (Parsing)** tokens.

### **Internal Mechanism:**

* **Signing Strategy**: It uses `HS256` (HMAC with SHA-256). It takes your `SECRET_KEY`, combines it with user data (Claims), and produces a unique hash.
* **The "Claims" Dictionary**:
* `subject`: The unique identifier (usually user email).
* `authorities`: A comma-separated string of roles (e.g., "ROLE_ADMIN, ROLE_USER").


* **Parsing (Modern JJWT 0.12+)**: Uses the new `parser().verifyWith(key).build()` pattern. It ensures the token hasn't been tampered with by checking the signature against the `SECRET_KEY`.

---

##  3. `JwtTokenValidator.java`: The Identity Check

This filter runs on **every single request** to verify if the user is who they say they are.

### **The Logic Flow:**

1. **Extraction**: Pulls the `Authorization` header.
2. **Bearer Check**: Ignores the request if it doesn't start with `Bearer ` (allowing public routes like `/auth/signup` to pass through).
3. **Validation**: If a token exists, it parses it. If the token is expired or the signature is wrong, it throws `BadCredentialsException`.
4. **Context Injection**: If valid, it converts the `"authorities"` claim back into a List of `GrantedAuthority` and places an `Authentication` object into the `SecurityContextHolder`.

> **Recall Note**: Once `SecurityContextHolder` is set, the rest of the application (Controllers/Services) "knows" who the user is without needing to check the database again.

---

##  4. `JwtConstant.java`: The Security Constants

A central place for sensitive keys and header names.

* **`SECRET_KEY`**: This must be a long, complex string. If leaked, anyone can forge tokens and become an Admin.
* **`JWT_HEADER`**: Standardized as `Authorization`.

---

##  Implementation Summary Table

| File | Main Bean/Method | Logic Purpose |
| --- | --- | --- |
| **`ApplicationConfig`** | `securityFilterChain` | Defines which URLs are public vs. private. |
| **`JwtProvider`** | `generateToken()` | Converts `Authentication` object into a Signed String. |
| **`JwtTokenValidator`** | `doFilterInternal()` | Intercepts requests to validate tokens before they reach Controllers. |
| **`JwtConstant`** | `SECRET_KEY` | The "Master Key" used for both signing and verifying. |

---


# Microservice Pillar 2: Data Persistence & Business Logic

This layer manages **User Identity** and provides the core logic required for authentication and profile management using **MongoDB** and **Spring Service** patterns.

---

##  1. `CustomerServiceImplementation`: The Security Bridge

This service is the most important bridge in your security architecture. It implements `UserDetailsService`, which is a core Spring Security interface.

### **The Internal "Handshake":**

When a user tries to log in, Spring Security calls `loadUserByUsername()`.

1. **Lookup**: It queries the `UserRepository` via email.
2. **Conversion**: It converts your database `User` object into a Spring Security `UserDetails` object.
3. **Verification**: Spring Security then takes the password from this `UserDetails` object and compares it with the password provided in the login request (using the `PasswordEncoder` defined in your config).

> **Recall Note:** If this service throws `UsernameNotFoundException`, the authentication flow stops immediately, and the user receives a "Bad Credentials" error.

---

##  2. `UserService`: Business Interface & Implementation

While `CustomerService` is for Spring Security, `UserService` is for **your application logic**.

### **Key Patterns Used:**

* **JWT-Based Lookup (`findUserProfileByJwt`)**:
* Instead of passing a User ID from the frontend (which can be faked), the frontend sends the JWT.
* The service extracts the email from the token, ensuring the user is only accessing **their own** data.


* **Separation of Concerns**: We use an **Interface** (`UserService`) and an **Implementation** (`UserServiceImplementation`). This allows us to swap the logic later (e.g., changing from MongoDB to PostgreSQL) without breaking the Controllers.

---

##  3. `UserRepository`: The Data Access Layer

We use **Spring Data MongoDB** to handle the connection to the cloud database.

* **MongoRepository**: Provides standard CRUD operations (`findAll`, `findById`, `save`) out of the box.
* **@Query Annotation**: While Spring can generate queries from method names, using `@Query("{email:?0}")` gives you explicit control over the MongoDB JSON query.

---

##  4. `application.yml`: The Infrastructure Blueprint

This file tells the microservice how to exist in the world.

### **Pillars of Configuration:**

* **Service Discovery (Eureka)**: The `eureka.client.service-url` points to port `8085`. This allows other services to find the "USER-SERVICE" by name instead of a hardcoded IP address.
* **Distributed Tracing (Zipkin & Micrometer)**:
* `sampling.probability: 1.0` means **100%** of requests are traced.
* When a request travels from the Gateway to the User Service, Zipkin creates a "Trace ID" to help you debug performance bottlenecks.


* **Cloud Database**: Points to **MongoDB Atlas**. Using `retryWrites=true` ensures the service is resilient to minor network flickers.

---

##  Implementation Summary Table

| Concept | File | Logic Purpose |
| --- | --- | --- |
| **UserDetailsService** | `CustomerServiceImplementation` | Bridges MongoDB data to Spring Security's auth engine. |
| **JWT Profile Lookup** | `UserServiceImplementation` | Securely identifies a user based on their "Passport" (token). |
| **Data Layer** | `UserRepository` | Handles JSON-based communication with MongoDB Atlas. |
| **Distributed Tracing** | `application.yml` | Enables observability so you can see request flow in Zipkin. |

---

This is the final chapter of your **User Microservice** documentation. It focuses on the "User Interface" of your backend—the REST APIs—and how the service protects itself from crashes using modern fault-tolerance patterns.

---

#  Microservice Pillar 3: API Layer, Models & Fault Tolerance

This layer defines how the outside world (Frontend or other Microservices) interacts with your data and how the system remains resilient under pressure.

---

##  1. Domain Model & Data Transfer Objects (DTOs)

The `User` model and various Request/Response classes define the shape of your data.

### **The User Model (`User.java`)**

* **`@Document(collection = "user")`**: Tells Spring Data that this class maps to a collection in MongoDB.
* **`@JsonProperty(access = Access.WRITE_ONLY)`**: **Critical Security Concept.** This ensures the password can be sent *to* the server (during signup), but is never sent *back* in a JSON response.
* **Default Roles**: Every new user starts with `ROLE_CUSTOMER` unless specified otherwise.

### **The DTO Pattern (`AuthResponse`, `LoginRequest`)**

We use DTOs to decouple the internal database model from what we show the user. For example, `AuthResponse` only contains the JWT and a success status, hiding internal user IDs or metadata.

---

##  2. Controller Layer: The Entry Points

Your controllers are divided by **Access Level**.

### **`AuthController`: The Public Entrance**

* **Sign-Up Logic**: Checks for existing emails, encodes the password using BCrypt, and immediately generates a JWT so the user is logged in instantly.
* **Sign-In Logic**: Validates credentials against the database and returns a fresh "Passport" (JWT).

### **`UserController`: The Private API**

* **`@RequestHeader("Authorization")`**: Instead of asking for a UserID, these methods take the JWT. This is the **Trustless** approach—the controller verifies the user's identity via the token on every call.
* **Password Masking**: Even though we have `WRITE_ONLY` in the model, the controller explicitly sets `user.setPassword(null)` before returning a profile as an extra layer of "Defense in Depth."

---

## 🛡 3. Fault Tolerance with Resilience4j

You have implemented the **Circuit Breaker** pattern in both controllers to handle "Cascading Failures."

### **How `@CircuitBreaker` Works Here:**

If the MongoDB database becomes slow or unresponsive:

1. **Monitor**: Resilience4j tracks the failure rate of `createUserHandler` or `signin`.
2. **Trip**: If failures exceed a threshold, the circuit "Opens."
3. **Fallback**: Instead of letting the thread hang and eventually crashing the server, the `signupFallback` method is called.
4. **User Experience**: The user gets a `503 Service Unavailable` with a clear message: *"AuthService is busy, Try again."*

---

##  4. Logging & Observability (`@Slf4j`)

You are using **Lombok's `@Slf4j**` for cleaner logging.

* **Info Logs**: Track successful events like "User successfully signup."
* **Error Logs**: In fallbacks, we log the `throwable.getMessage()`. This is vital for **Production Debugging**—it tells you *why* the circuit tripped (e.g., "Connection Timeout").

---

##  Implementation Summary Table

| Component | Annotation/Logic | Learning Recall |
| --- | --- | --- |
| **User Model** | `@Id` & `@Document` | Defines the identity and storage in the NoSQL database. |
| **Auth Controller** | `@PostMapping("/signup")` | The only place where raw passwords are converted to hashes. |
| **Circuit Breaker** | `fallbackMethod = "..."` | The "Emergency Exit" logic that keeps the service alive. |
| **User Controller** | `@RequestHeader` | Enforces that the client must provide a valid JWT to see data. |
| **Home Controller** | `permitAll()` | Useful for "Health Checks" by Eureka or Load Balancers. |

---

### **Summary of the User Service**

By reading these three README sections, you now have a complete technical map of a **Secure, Stateless, and Resilient Microservice**.

