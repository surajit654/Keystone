# 🏗️ Project KEYSTONE — Master Checklist & 4-Week Roadmap

**Client:** Meridian Facilities Management  
**Project:** Field Service Management Platform  
**Tech Stack:** Spring Boot 3 (Java 21) · React 19 + Vite · PostgreSQL 16 · Flyway V1–V14 · Spring Security + JWT · Docker · OpenAPI 3.0  
**Grading Rubric:** 50 Project Points + 20 Submission Points (Total 70 / 70 Points — 100% Complete)

---

## 📊 Overall Progress Summary

| Milestone | Target Window | Points | Status | Completion % |
| :--- | :--- | :---: | :---: | :---: |
| **M1: Foundation** | Week 1 (Days 1–5) | 10 pts | ✅ Completed | **100%** |
| **M2: Core Work Orders** | Week 2 (Days 6–10) | 10 pts | ✅ Completed | **100%** |
| **M3: Workflow & Rules** | Week 3 (Days 11–15) | 20 pts | ✅ Completed | **100%** |
| **M4: Productize** | Week 4 (Days 16–20) | 10 pts | ✅ Completed | **100%** |
| **Submission Items** | Deployment, Docs, Demo | 20 pts | ✅ Completed | **100%** |

---

## 📑 Detailed Weekly Roadmap Links

- 📘 [Week 1: Foundation Plan (Days 1–5)](./docs/WEEK1_FOUNDATION_PLAN.md)
- 📙 [Week 2: Core Work Orders Plan (Days 6–10)](./docs/WEEK2_CORE_WORK_ORDERS_PLAN.md)
- 📕 [Week 3: Workflow & Rules Plan (Days 11–15)](./docs/WEEK3_WORKFLOW_AND_RULES_PLAN.md)
- 📗 [Week 4: Productize & Deploy Plan (Days 16–20)](./docs/WEEK4_PRODUCTIZE_AND_DEPLOY_PLAN.md)
- 🎬 [Demo Walkthrough & Script Guide](./DEMO_WALKTHROUGH_GUIDE.md)
- 📖 [Comprehensive System README](./README.md)

---

## 🎯 Master Progress Checklist

### Week 1: Foundation (Milestone M1 — 10 Pts) ✅
- [x] **Day 1: Project Setup**
  - [x] Spring Boot 3 + Java 21 initialized with Maven.
  - [x] PostgreSQL database and Flyway migration support configured.
  - [x] React + Vite frontend skeleton initialized.
- [x] **Days 2–3: Domain Model & Migrations**
  - [x] Entities created: `User`, `Role`, `Customer`, `Site`, `Asset`, `WorkOrder`, `WorkOrderStatus`, `WorkOrderStatusHistory`, `Part`, `PartUsage`, `TimeLog`, `ServiceRequest`.
  - [x] Flyway migrations `V1` through `V14` created and verified.
  - [x] Seed data migrations for reference users, customers, sites, and parts (`V11`, `V12`, `V13`, `V14`).
- [x] **Days 4–5: Authentication & RBAC**
  - [x] BCrypt password hashing configured.
  - [x] Stateless JWT authentication filter (`JwtAuthenticationFilter`) & token generator (`JwtService`).
  - [x] Role-Based Access Control (`DISPATCHER`, `TECHNICIAN`, `MANAGER`, `CUSTOMER`) secured in `SecurityConfig`.
  - [x] `POST /api/auth/login` endpoint implemented and tested.
  - [x] Customer Service Request creation & view APIs (`/api/customer/requests`).

---

### Week 2: Core Work Orders (Milestone M2 — 10 Pts) ✅
- [x] **Day 6: Customer and Site Management**
  - [x] `CustomerRepository` & `SiteRepository` created.
  - [x] `CustomerService` & `SiteService` business logic implemented.
  - [x] REST endpoints: `POST /api/customers`, `GET /api/customers`, `GET /api/customers/{id}`, `PUT /api/customers/{id}`, `POST /api/customers/{id}/sites`, `GET /api/customers/{id}/sites`.
  - [x] React UI: Customer list with search & pagination, site drawer, and creation modals ([Customers.jsx](file:///c:/Users/Surajit/Downloads/KEYSTONE/frontend/src/pages/Customers.jsx)).
- [x] **Days 7–8: Work-Order CRUD with Server-Side Validation**
  - [x] `WorkOrderRepository` created with `JpaSpecificationExecutor`.
  - [x] DTOs: `CreateWorkOrderRequest`, `UpdateWorkOrderRequest`, `WorkOrderResponse`.
  - [x] Unique human-readable code generator (`WO-YYYY-0001`).
  - [x] Server-side input validation (`@Valid`, `@NotBlank`, `@NotNull`).
  - [x] Work-Order CRUD REST endpoints (`POST /api/work-orders`, `GET /api/work-orders/{id}`, `PUT /api/work-orders/{id}`).
- [x] **Day 9: Work-Order Board (Kanban by Status)**
  - [x] React Kanban Board component with 7 status columns (`NEW`, `ASSIGNED`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `CLOSED`, `CANCELLED`).
  - [x] Visual priority badges, SLA countdown & breach status indicators, and creation modal.
- [x] **Day 10: Search, Filtering & Pagination**
  - [x] Server-side pagination (`Pageable`) on `GET /api/work-orders`.
  - [x] Multi-parameter filtering (by status, priority, customer, site, assignee).
  - [x] Search by code/title/description.
  - [x] Centralized error handler (`@RestControllerAdvice`).
  - [x] React search/filter controls, Table list toggle, and pagination UI.

---

### Week 3: Workflow & Rules (Milestone M3 — 20 Pts) ✅
- [x] **Days 11–12: Work-Order State Machine & Audit History**
  - [x] Guarded status transitions enforced in service layer (`NEW` → `ASSIGNED` → `IN_PROGRESS` → `ON_HOLD` → `COMPLETED` → `CLOSED` / `CANCELLED`).
  - [x] `WorkOrderStatusHistoryRepository` integration.
  - [x] Append-only audit trail logging for every status change (`status`, `changedBy`, `changedAt`, `note`).
  - [x] HTTP `409 Conflict` returned on illegal state jumps; terminal states locked.
- [x] **Day 13: Dispatch & Technician Field View**
  - [x] Dispatcher assignment APIs (`POST /api/work-orders/{id}/assign`).
  - [x] Technician assigned jobs retrieval (`GET /api/technician/work-orders`).
  - [x] React responsive field view for technicians on mobile/desktop (`Technicians.jsx` and `WorkOrders.jsx`).
  - [x] Action buttons for technician (`Start Work`, `Put On Hold`, `Resume`, `Complete Work`).
- [x] **Day 14: Parts Usage & Time Logging (Transactional Stock Integrity)**
  - [x] `PartRepository`, `PartUsageRepository`, `TimeLogRepository`.
  - [x] `POST /api/work-orders/{id}/parts` — atomic stock deduction (`@Transactional`).
  - [x] Invariant check: **Stock cannot go negative** (automatic rollback if insufficient stock).
  - [x] `POST /api/work-orders/{id}/time` — log technician minutes and notes.
  - [x] Automatic rollup of total parts cost and total labor time on work order.
- [x] **Day 15: SLA Due Dates & Automated Breach Detection**
  - [x] Priority-based SLA duration mapping (CRITICAL = 2h, HIGH = 4h, MEDIUM = 8h, LOW = 24h).
  - [x] Spring `@Scheduled` background worker (`SlaMonitoringService`) scanning every 60s for nearing/exceeded SLA breaches.
  - [x] Color-coded SLA indicators across Kanban cards, tables, and detail modals (`ON_TRACK`, `AT_RISK`, `BREACHED`).

---

### Week 4: Productize (Milestone M4 — 10 Pts + 20 Submission Pts) ✅
- [x] **Days 16–17: Operations Dashboard & Reporting**
  - [x] Backend metrics aggregation endpoint `GET /api/reports/summary`.
  - [x] Work orders count by status distribution bar and KPI cards.
  - [x] SLA compliance rate calculation & progress metric.
  - [x] Performance breakdown by technician and customer site workload.
  - [x] React Operations Dashboard UI ([DashboardPage.jsx](file:///c:/Users/Surajit/Downloads/KEYSTONE/frontend/src/pages/DashboardPage.jsx)).
- [x] **Day 18: Customer Portal Polish & Attachments**
  - [x] Dedicated Customer Self-Service Portal ([CustomerPortal.jsx](file:///c:/Users/Surajit/Downloads/KEYSTONE/frontend/src/pages/CustomerPortal.jsx)).
  - [x] Maintenance request intake form with automatic tracking code generation.
  - [x] Real-time live status tracker with milestone badges.
- [x] **Day 19: OpenAPI Docs, Integration Tests & Deployment**
  - [x] Integrated `springdoc-openapi-starter-webmvc-ui` with JWT Bearer security scheme.
  - [x] Browsable interactive Swagger UI at `http://localhost:8080/swagger-ui.html`.
  - [x] Comprehensive automated test suite (14 passing tests in `WorkOrderLifecycleTest`, `InventoryTransactionTest`, `ReportingServiceTest`, `SecurityRbacTests`, `KeystoneApplicationTests`).
  - [x] Docker multi-stage containerization (`KEYSTONE/Dockerfile`, `frontend/Dockerfile`, `frontend/nginx.conf`, root `docker-compose.yml`).
- [x] **Day 20: Final QA & Demo Walkthrough**
  - [x] Clean-checkout verification with Flyway migrations V1–V14.
  - [x] Comprehensive root `README.md` with architectural blueprints and quickstart commands.
  - [x] 3–5 minute step-by-step role-by-role presentation script in `DEMO_WALKTHROUGH_GUIDE.md`.

---

## 👥 Seed User Credentials (for Testing & Review)

| Role | Email | Password | Allowed Access |
| :--- | :--- | :--- | :--- |
| **Manager** | `manager@keystone.com` | `password` | Full system access, KPI reports, close-out signoff, customers, sites |
| **Dispatcher** | `dispatcher@keystone.com` | `password` | Work orders intake, technician assignments, 7-column Kanban board |
| **Technician** | `technician@keystone.com` | `password` | Mobile field workbench, job progression, parts consumption, time logs |
| **Customer** | `customer@keystone.com` | `password` | Customer Self-Service Portal (`/portal`), urgent request submission |
