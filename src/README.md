# 🚀 High-Performance Async Task Management System

A production-ready **Spring Boot 3.x** application demonstrating a decoupled, non-blocking architecture. This project solves the challenge of handling slow external API dependencies by implementing a robust **Producer-Consumer pipeline** with multi-level caching and security.

---

## 🏗️ Architectural Design

The system is designed to provide maximum responsiveness to the user by separating data intake from background processing.



### 1. The Producer (Web Layer)
* **REST Controller**: Acts as the secure gateway, utilizing **Spring Security (Basic Auth)** and **Bean Validation** (`@Valid`) to ensure data integrity.
* **Service Layer**: Persists the task to **MySQL** immediately via Spring Data JPA to ensure durability.
* **Hand-off**: Pushes the task into a thread-safe memory buffer and returns a `200 OK` to the user in milliseconds.

### 2. The Buffer (Memory Layer)
* **Thread-Safety**: Implements a **`ConcurrentLinkedQueue`**. Unlike a standard `ArrayList`, this lock-free data structure allows multiple web threads to add tasks while the worker thread polls them, preventing `ConcurrentModificationException`.

### 3. The Consumer (Async Processing Layer)
* **Smart Polling**: A `@Scheduled` heartbeat triggers every 10 seconds to check the queue.
* **Resource Management**: Using **`@Async`**, the workload is handed off to a custom **`ThreadPoolTaskExecutor`**.
* **External Integration**: Uses `RestTemplate` to sync data with external systems without blocking the main application.

---

## 🛠️ Technical Stack & Implementation

### 🔐 Security & Access Control (`SecurityConfig.java`)
* **Stateless Auth**: Configured for HTTP Basic Authentication.
* **CSRF Mitigation**: Intentionally disabled. Since this is a stateless REST API (no session cookies), it is natively protected against CSRF, facilitating easier integration with mobile and CLI clients.

### 🧵 Async & Thread Pooling (`AsyncConfig.java`)
To prevent "Thread Exhaustion" and protect system memory:
* **Core Pool Size**: 5 (Threads always ready for work).
* **Max Pool Size**: 10 (Burst capacity for peak traffic).
* **Queue Capacity**: 500 (Backlog limit to prevent OutOfMemory errors).
* **Observability**: Threads are prefixed with `TaskThread-` for professional log tracing.



### ⚡ Caching Strategy (`Caffeine`)
* **Technology**: Caffeine (High-performance Java L1 Cache).
* **TTL (Time-To-Live)**: 60 Seconds.
* **Logic**: GET requests for task status are served from RAM via the `@Cacheable` proxy, reducing MySQL CPU load and database I/O.



---

## 🚦 API References

### 1. Submit a Task
**POST** `/api/tasks`  
**Auth**: Basic Auth (`user` / `password`)
```json
{
  "name": "Cloud Data Sync",
  "description": "Triggering a background sync between MySQL and S3"
}