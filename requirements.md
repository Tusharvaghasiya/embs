## Backend Requirements: Smart Event Management & Booking System

**Version:** 1.0
**Date:** October 26, 2023

**1. Introduction & Overview**

The "Smart Event Management & Booking System" (SEMBS) backend will provide APIs for managing events, user accounts, and bookings. It aims to be a robust platform for event organizers to list their events and for attendees to discover and book them. The system will focus on performance, security, and data integrity.

**Key Learning Objectives for You:**
*   Designing a relational database schema with various relationship types (one-to-many, many-to-many).
*   Implementing robust CRUD operations with business logic and validation.
*   Securing APIs using role-based access control (RBAC).
*   Implementing effective caching strategies.
*   Handling transactions and ensuring data consistency.
*   Designing and documenting RESTful APIs.
*   Writing unit and integration tests.

**2. Core Entities & Database Design Considerations**

The database should be designed to be normalized to at least 3NF where appropriate, with clear primary keys, foreign keys, indexes, and constraints. Consider using PostgreSQL for its rich feature set (like ENUM types, JSONB, GIS extensions if you want to add location-based search later).

*   **User (`users`)**
    *   `id` (UUID, Primary Key)
    *   `username` (VARCHAR(50), Unique, Not Null, Indexed)
    *   `email` (VARCHAR(255), Unique, Not Null, Indexed)
    *   `password_hash` (VARCHAR(255), Not Null)
    *   `first_name` (VARCHAR(100))
    *   `last_name` (VARCHAR(100))
    *   `role` (ENUM('ORGANIZER', 'ATTENDEE', 'ADMIN'), Not Null, Default: 'ATTENDEE')
    *   `is_active` (BOOLEAN, Default: true) - For soft deletes/account disabling
    *   `created_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   `updated_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   *DB Constraints*: Unique constraints on `username` and `email`.
    *   *DB Indexes*: On `username`, `email`, `role`.

*   **Event (`events`)**
    *   `id` (UUID, Primary Key)
    *   `title` (VARCHAR(255), Not Null, Indexed - for full-text search later)
    *   `description` (TEXT, Not Null)
    *   `start_date_time` (TIMESTAMP WITH TIME ZONE, Not Null)
    *   `end_date_time` (TIMESTAMP WITH TIME ZONE, Not Null)
    *   `venue_name` (VARCHAR(255), Not Null)
    *   `address_line1` (VARCHAR(255))
    *   `city` (VARCHAR(100), Indexed)
    *   `country` (VARCHAR(100), Indexed)
    *   `postal_code` (VARCHAR(20))
    *   `latitude` (DECIMAL(9,6)) - Optional, for map integration
    *   `longitude` (DECIMAL(9,6)) - Optional
    *   `capacity` (INTEGER, Not Null, Min: 1)
    *   `current_attendee_count` (INTEGER, Default: 0, Not Null) - Denormalized for quick checks, update via triggers or application logic.
    *   `organizer_id` (UUID, Foreign Key to `users.id`, Not Null)
    *   `status` (ENUM('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED', 'SOLD_OUT'), Not Null, Default: 'DRAFT', Indexed)
    *   `created_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   `updated_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   *DB Constraints*: `end_date_time` > `start_date_time`. `organizer_id` must reference a user with 'ORGANIZER' or 'ADMIN' role (can be enforced at application level or via complex DB constraint/trigger). `current_attendee_count` <= `capacity`.
    *   *DB Indexes*: On `start_date_time`, `city`, `country`, `status`, `organizer_id`.

*   **Category (`categories`)**
    *   `id` (SERIAL, Primary Key) - Simpler for categories as they are less dynamic.
    *   `name` (VARCHAR(100), Unique, Not Null, Indexed)
    *   `description` (TEXT)
    *   `created_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   `updated_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)

*   **Event_Category (Join Table for Many-to-Many: `event_categories`)**
    *   `event_id` (UUID, Foreign Key to `events.id`, Not Null)
    *   `category_id` (INTEGER, Foreign Key to `categories.id`, Not Null)
    *   PRIMARY KEY (`event_id`, `category_id`)
    *   *DB Indexes*: On `event_id` and `category_id` individually for efficient lookups from either side.

*   **Booking (`bookings`)**
    *   `id` (UUID, Primary Key)
    *   `event_id` (UUID, Foreign Key to `events.id`, Not Null)
    *   `attendee_id` (UUID, Foreign Key to `users.id`, Not Null)
    *   `booking_date_time` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   `number_of_tickets` (INTEGER, Not Null, Default: 1, Min: 1)
    *   `status` (ENUM('CONFIRMED', 'CANCELLED_BY_USER', 'CANCELLED_BY_ORGANIZER', 'WAITLISTED'), Not Null, Default: 'CONFIRMED')
    *   `created_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   `updated_at` (TIMESTAMP WITH TIME ZONE, Default: CURRENT_TIMESTAMP)
    *   *DB Constraints*: Composite Unique Key on (`event_id`, `attendee_id`) to prevent duplicate bookings by the same user for the same event (unless business logic allows multiple distinct bookings by same user).
    *   *DB Indexes*: On `event_id`, `attendee_id`, `status`.

**3. Functional Requirements (API Endpoints)**

All endpoints should return appropriate HTTP status codes and consistent JSON response structures (e.g., `{"data": ..., "error": ...}`). Timestamps should be in ISO 8601 format.

**3.1. Authentication & User Management (`/api/v1/auth`, `/api/v1/users`)**

*   **`POST /api/v1/auth/register`**
    *   Description: Register a new user (default role: 'ATTENDEE').
    *   Request Body: `username`, `email`, `password`, `first_name`, `last_name`.
    *   Response: User details (excluding password) or error message.
    *   Security: Public.
    *   Validation: Strong password, unique username/email, valid email format.

*   **`POST /api/v1/auth/login`**
    *   Description: Authenticate a user.
    *   Request Body: `username` or `email`, `password`.
    *   Response: JWT (Access Token, Refresh Token), user details (id, username, role).
    *   Security: Public.

*   **`POST /api/v1/auth/refresh-token`**
    *   Description: Obtain a new access token using a refresh token.
    *   Request Body: `refreshToken`.
    *   Response: New JWT (Access Token).
    *   Security: Public (but requires valid refresh token).

*   **`GET /api/v1/users/me`**
    *   Description: Get details of the currently authenticated user.
    *   Response: User details (excluding password).
    *   Security: Authenticated (Any role).

*   **`PUT /api/v1/users/me`**
    *   Description: Update details of the currently authenticated user.
    *   Request Body: `first_name`, `last_name`, `email` (if email changed, might require re-verification - out of scope for v1).
    *   Response: Updated user details.
    *   Security: Authenticated (Any role).

*   **`GET /api/v1/users/{userId}` (Admin)**
    *   Description: Get details of any user by ID.
    *   Security: ADMIN role.

*   **`PUT /api/v1/users/{userId}/role` (Admin)**
    *   Description: Update a user's role.
    *   Request Body: `{"role": "ORGANIZER"}`
    *   Security: ADMIN role. (Careful with changing self to non-admin).

**3.2. Category Management (`/api/v1/categories`) (Admin)**

*   **`POST /api/v1/categories`**
    *   Description: Create a new category.
    *   Request Body: `name`, `description`.
    *   Security: ADMIN role.
    *   Cache: Invalidate category list cache.

*   **`GET /api/v1/categories`**
    *   Description: List all categories.
    *   Response: Paginated list of categories.
    *   Security: Public.
    *   Cache: Cache this response (e.g., `all_categories_page_X`).

*   **`GET /api/v1/categories/{categoryId}`**
    *   Description: Get a specific category by ID.
    *   Security: Public.
    *   Cache: Cache this response (e.g., `category_{categoryId}`).

*   **`PUT /api/v1/categories/{categoryId}`**
    *   Description: Update a category.
    *   Security: ADMIN role.
    *   Cache: Invalidate `category_{categoryId}` and category list cache.

*   **`DELETE /api/v1/categories/{categoryId}`**
    *   Description: Delete a category. (Consider soft delete or what happens to events using it - perhaps disallow if in use).
    *   Security: ADMIN role.
    *   Cache: Invalidate relevant caches.

**3.3. Event Management (`/api/v1/events`)**

*   **`POST /api/v1/events`**
    *   Description: Create a new event. Organizer ID is taken from the authenticated user.
    *   Request Body: Event details (title, description, dates, venue, capacity, category IDs).
    *   Response: Created event details.
    *   Security: ORGANIZER or ADMIN role.
    *   Business Logic: Ensure `start_date_time` is in the future.
    *   DB: Store event, associate with categories in `event_categories`.

*   **`GET /api/v1/events`**
    *   Description: List/Search events.
    *   Query Params: `page`, `size`, `sortBy` (e.g., `start_date_time_asc`), `category_id`, `city`, `country`, `date_from`, `date_to`, `status` (default 'PUBLISHED'), `organizer_id`.
    *   Response: Paginated list of PUBLISHED events by default. Organizers can see their DRAFT events. Admins can see all.
    *   Security: Public for PUBLISHED events. ORGANIZER can see their own DRAFT/CANCELLED etc. ADMIN can see all.
    *   Cache: Cache results based on query parameters for public queries.
    *   DB: Efficient querying using indexes.

*   **`GET /api/v1/events/{eventId}`**
    *   Description: Get details of a specific event.
    *   Response: Event details, including organizer info (basic) and categories.
    *   Security: Public if event is PUBLISHED. ORGANIZER (owner) or ADMIN can view any status.
    *   Cache: Cache published event details (e.g., `event_{eventId}`).

*   **`PUT /api/v1/events/{eventId}`**
    *   Description: Update an event.
    *   Request Body: Fields to update.
    *   Response: Updated event details.
    *   Security: ORGANIZER (owner) or ADMIN.
    *   Business Logic: Cannot update if event is 'COMPLETED' or 'CANCELLED' (unless admin override). Capacity cannot be reduced below `current_attendee_count`.
    *   Cache: Invalidate `event_{eventId}` and relevant list caches.

*   **`DELETE /api/v1/events/{eventId}`**
    *   Description: Delete an event (soft delete by setting status to 'CANCELLED' or a new 'DELETED' status). Actual deletion only if no bookings.
    *   Security: ORGANIZER (owner) or ADMIN.
    *   Business Logic: If event has bookings, it should be 'CANCELLED', not deleted. Notify attendees (out of scope for API, but good to note).
    *   Cache: Invalidate relevant caches.

*   **`PATCH /api/v1/events/{eventId}/status`**
    *   Description: Change event status (e.g., DRAFT -> PUBLISHED, PUBLISHED -> CANCELLED).
    *   Request Body: `{"status": "PUBLISHED"}`
    *   Security: ORGANIZER (owner) or ADMIN.
    *   Business Logic: Transitions must be valid (e.g., cannot go from CANCELLED to PUBLISHED easily).
    *   Cache: Invalidate relevant caches.

**3.4. Booking Management (`/api/v1/bookings`, `/api/v1/events/{eventId}/bookings`)**

*   **`POST /api/v1/events/{eventId}/bookings`**
    *   Description: Create a booking for an event. Attendee ID is from authenticated user.
    *   Request Body: `number_of_tickets` (default 1).
    *   Response: Booking confirmation details.
    *   Security: ATTENDEE role.
    *   Business Logic:
        *   Event must be 'PUBLISHED'.
        *   Event `start_date_time` must be in the future.
        *   Check `event.capacity` vs `event.current_attendee_count + number_of_tickets`.
        *   If full, can optionally add to a waitlist (set status to 'WAITLISTED' - advanced).
        *   Operation must be transactional: create booking AND update `event.current_attendee_count`.
    *   Cache: Invalidate event attendee count related caches if any.

*   **`GET /api/v1/users/me/bookings`**
    *   Description: List all bookings for the authenticated user.
    *   Query Params: `page`, `size`, `status`.
    *   Response: Paginated list of bookings.
    *   Security: Authenticated (Any role, but typically ATTENDEE).

*   **`GET /api/v1/events/{eventId}/bookings`**
    *   Description: List all bookings for a specific event.
    *   Query Params: `page`, `size`, `status`.
    *   Response: Paginated list of bookings (attendee info might be minimal for privacy).
    *   Security: ORGANIZER (owner of the event) or ADMIN.

*   **`GET /api/v1/bookings/{bookingId}`**
    *   Description: Get details of a specific booking.
    *   Response: Booking details.
    *   Security: ATTENDEE (owner of booking), ORGANIZER (owner of the event), or ADMIN.

*   **`DELETE /api/v1/bookings/{bookingId}` (Cancel Booking)**
    *   Description: Cancel a booking. Sets status to 'CANCELLED_BY_USER'.
    *   Security: ATTENDEE (owner of booking) or ORGANIZER (owner of the event, sets to 'CANCELLED_BY_ORGANIZER').
    *   Business Logic:
        *   Update booking status.
        *   Decrement `event.current_attendee_count` by `number_of_tickets`.
        *   Transactional operation.
        *   If event was 'SOLD_OUT', change status to 'PUBLISHED'.
        *   (Advanced: If waitlist exists, notify next person).
    *   Cache: Invalidate event attendee count.

**4. Non-Functional Requirements**

*   **4.1. Security**
    *   Authentication: JWT (JSON Web Tokens) for stateless authentication.
    *   Authorization: Spring Security with Role-Based Access Control (RBAC).
    *   Password Storage: BCrypt hashing.
    *   Input Validation: Bean Validation (JSR 380) on all DTOs.
    *   HTTPS: To be enforced by deployment environment (e.g., Nginx, Load Balancer).
    *   Protection against common vulnerabilities (OWASP Top 10 where applicable to backend).

*   **4.2. Performance & Scalability**
    *   Response Time: Average API response time < 200ms for 95th percentile under normal load.
    *   Database: Proper indexing, connection pooling. Avoid N+1 query problems.
    *   Statelessness: Backend services should be stateless to allow horizontal scaling.
    *   **Caching:**
        *   **Strategy:** Cache-Aside pattern.
        *   **Technology:** Spring Cache Abstraction with an in-memory cache (e.g., Caffeine, EhCache) for single-node, or a distributed cache (e.g., Redis, Hazelcast) for multi-node deployments. Start with Caffeine.
        *   **What to Cache:**
            *   Frequently read, rarely updated data: `GET /api/v1/categories`, `GET /api/v1/categories/{categoryId}`.
            *   Published Event Details: `GET /api/v1/events/{eventId}` for published events.
            *   Event Lists: `GET /api/v1/events` with common filter combinations (cache key should include all query params).
            *   User details (for `GET /users/me` or by ID if frequently accessed internally).
        *   **Cache Invalidation:**
            *   On `POST`, `PUT`, `PATCH`, `DELETE` operations for an entity, invalidate its individual cache entry and relevant list caches.
            *   Example: Updating an event invalidates `event_{eventId}` cache and potentially `events_list_params_X` caches.
            *   Consider Time-To-Live (TTL) for caches, especially for lists.

*   **4.3. Reliability & Error Handling**
    *   Global Exception Handler: Centralized handling of exceptions, returning standardized JSON error responses.
    *   Transactional Integrity: Use `@Transactional` appropriately, especially for operations involving multiple DB writes (e.g., creating a booking and updating event attendee count).
    *   Logging: Structured logging (e.g., SLF4j + Logback/Log4j2). Log requests, errors, key business events.

*   **4.4. Maintainability & Development**
    *   Code Quality: Clean code, SOLID principles, appropriate design patterns.
    *   API Documentation: OpenAPI/Swagger for automatic API documentation.
    *   Testing:
        *   Unit Tests (JUnit, Mockito) for service layer logic.
        *   Integration Tests (Spring Boot Test, Testcontainers or H2) for API endpoints and DB interactions.
        *   Target >70% code coverage.
    *   Build Tool: Maven or Gradle.
    *   Version Control: Git.

**5. Technical Stack (Suggested)**
*   Language: Java 17+
*   Framework: Spring Boot 3.x
*   Data Access: Spring Data JPA (Hibernate)
*   Database: PostgreSQL (preferred) or MySQL
*   Security: Spring Security
*   Caching: Spring Cache + Caffeine (initially)
*   API Docs: Springdoc OpenAPI
*   Build: Maven or Gradle

**6. Assumptions**
*   User email verification and password reset flows are out of scope for V1.
*   Payment processing is out of scope for V1.
*   Real-time notifications (e.g., event cancelled) are out of scope for V1.
*   Advanced features like event reviews, waitlists (basic implementation can be done), or complex search (GIS, full-text beyond simple title match) are enhancements for future versions.

**7. Out of Scope (for this initial robust version)**
*   Frontend UI development.
*   Email/SMS notifications.
*   Payment gateway integration.
*   Advanced analytics and reporting.
*   Full-text search beyond basic LIKE queries (unless you want to integrate Elasticsearch/Solr).
*   File uploads (e.g., event images).

This set of requirements should provide a solid foundation and a good challenge for building a non-trivial Spring Boot backend application. Good luck!
