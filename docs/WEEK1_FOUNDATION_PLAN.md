# 📘 Week 1: Foundation Plan (Days 1–5)

**Milestone:** M1 — Foundation  
**Score Weight:** 10 Project Points  
**Goal:** Running skeleton with Auth, Domain Model, Migrations, and Seed Data on PostgreSQL.

---

## 📅 Day-by-Day Plan & Status

### Day 1: Project Setup & Baseline Infrastructure
- [x] **Step 1.1**: Initialize Spring Boot 3 with Java 21 LTS (Web, Data JPA, Security, PostgreSQL, Validation).
- [x] **Step 1.2**: Configure `application.properties` with PostgreSQL datasource and Flyway migration support.
- [x] **Step 1.3**: Initialize React + Vite frontend application in `/frontend`.
- [x] **Step 1.4**: Configure CORS policy in `CorsConfig.java` to allow frontend communication (`http://localhost:5173`).

### Days 2–3: Domain Entity Modeling & Flyway Migrations
- [x] **Step 2.1**: Design database schema with Flyway migrations:
  - `V1__init.sql`
  - `V2__create_users.sql`
  - `V3__create_customers.sql`
  - `V4__create_sites.sql`
  - `V5__create_assets.sql`
  - `V6__create_work_orders.sql`
  - `V7__create_work_order_status_history.sql`
  - `V8__create_parts.sql`
  - `V9__create_part_usage.sql`
  - `V10__create_time_logs.sql`
  - `V13__create_service_requests.sql`
- [x] **Step 2.2**: Seed reference users and sample parts data (`V11__seed_reference_data.sql`, `V12__update_seed_passwords.sql`).
- [x] **Step 2.3**: Implement JPA Entities:
  - `User`, `Role` (Enum: `DISPATCHER`, `TECHNICIAN`, `MANAGER`, `CUSTOMER`)
  - `Customer`, `Site`, `Asset`
  - `WorkOrder`, `WorkOrderStatus`, `WorkOrderStatusHistory`
  - `Part`, `PartUsage`, `TimeLog`
  - `ServiceRequest`

### Days 4–5: Authentication, RBAC & Customer Service Requests
- [x] **Step 3.1**: Implement BCrypt password encoder in `SecurityConfig.java`.
- [x] **Step 3.2**: Implement `JwtService.java` for HMAC-SHA256 token generation and validation.
- [x] **Step 3.3**: Create `JwtAuthenticationFilter.java` for stateless per-request authorization.
- [x] **Step 3.4**: Implement `AuthService` and `AuthController` (`POST /api/auth/login`).
- [x] **Step 3.5**: Secure endpoint hierarchies with role authorization in `SecurityConfig`:
  - `/api/customer/**` → `ROLE_CUSTOMER`
  - `/api/dispatcher/**` → `ROLE_DISPATCHER`
  - `/api/technician/**` → `ROLE_TECHNICIAN`
  - `/api/manager/**` → `ROLE_MANAGER`
- [x] **Step 3.6**: Implement Customer request pipeline (`POST /api/customer/requests`, `GET /api/customer/requests`).

---

## 🧪 Acceptance & Verification
- **All 36 Java source files compiled** without errors.
- **Milestone M1 Review**: 100% Passed.
