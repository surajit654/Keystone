# 📙 Week 2: Core Work Orders Plan (Days 6–10)

**Milestone:** M2 — Core Work Orders  
**Score Weight:** 10 Project Points  
**Goal:** Work orders end to end: Create, List, Board (Kanban), Search, Filter, and Pagination with full validation.

---

## 📅 Granular Day-by-Day Task Breakdown

### 🔹 Day 6: Customer & Site Management (API + UI)

#### Task 6.1: Repositories
- [x] **Task 6.1.1**: Create `CustomerRepository` extending `JpaRepository<Customer, Long>` with pagination and search methods (`findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase`).
- [x] **Task 6.1.2**: Create `SiteRepository` extending `JpaRepository<Site, Long>` with `findByCustomerId(Long customerId)` and `findByCustomerIdAndNameContainingIgnoreCase`.

#### Task 6.2: DTOs
- [x] **Task 6.2.1**: Create `CreateCustomerRequest` (`@NotBlank name`, `@Email email`, `phone`).
- [x] **Task 6.2.2**: Create `CustomerResponse` (`id`, `name`, `email`, `phone`).
- [x] **Task 6.2.3**: Create `CreateSiteRequest` (`@NotBlank name`, `address`, `city`).
- [x] **Task 6.2.4**: Create `SiteResponse` (`id`, `name`, `address`, `city`, `customerId`, `customerName`).

#### Task 6.3: Services & Business Logic
- [x] **Task 6.3.1**: Implement `CustomerService`:
  - `createCustomer(CreateCustomerRequest req)`
  - `getAllCustomers(Pageable pageable, String search)`
  - `getCustomerById(Long id)`
  - `updateCustomer(Long id, CreateCustomerRequest req)`
- [x] **Task 6.3.2**: Implement `SiteService`:
  - `createSite(Long customerId, CreateSiteRequest req)` (verifies customer exists)
  - `getSitesByCustomer(Long customerId)`
  - `getSiteById(Long siteId)`

#### Task 6.4: REST Controllers & Security
- [x] **Task 6.4.1**: Create `CustomerApiController`:
  - `POST /api/customers` (`201 Created` — Dispatcher & Manager)
  - `GET /api/customers` (`200 OK` — Paginated list)
  - `GET /api/customers/{id}` (`200 OK`)
  - `PUT /api/customers/{id}` (`200 OK`)
  - `POST /api/customers/{id}/sites` (`201 Created`)
  - `GET /api/customers/{id}/sites` (`200 OK`)
- [x] **Task 6.4.2**: Update `SecurityConfig` with role authorization for `/api/customers/**`.

#### Task 6.5: React UI for Customers & Sites
- [x] **Task 6.5.1**: Customer list component with search and pagination (`Customers.jsx`).
- [x] **Task 6.5.2**: Site list and creation modal under selected customer organization.

---

### 🔹 Days 7–8: Work-Order CRUD with Server-Side Validation & DTOs

#### Task 7.1: Model & Enum Refinement
- [x] **Task 7.1.1**: Update `WorkOrderStatus` to support `NEW`, `OPEN`, `ASSIGNED`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `CLOSED`, `CANCELLED`.
- [x] **Task 7.1.2**: Create `WorkOrderRepository` with query methods, count prefix, and `JpaSpecificationExecutor`.

#### Task 7.2: Code Generation & SLA Computation
- [x] **Task 7.2.1**: Implement atomic human-readable code generator in `WorkOrderService` (`WO-YYYY-0001`, `WO-YYYY-0002`...).
- [x] **Task 7.2.2**: Implement priority-based SLA calculation helper:
  - `HIGH` → `now + 4 hours`
  - `MEDIUM` → `now + 24 hours`
  - `LOW` → `now + 48 hours`

#### Task 7.3: DTOs & Validation
- [x] **Task 7.3.1**: Create `CreateWorkOrderRequest` (`@NotBlank title`, `description`, `@NotBlank priority`, `@NotNull customerId`, `@NotNull siteId`).
- [x] **Task 7.3.2**: Create `UpdateWorkOrderRequest` (`title`, `description`, `priority`, `siteId`).
- [x] **Task 7.3.3**: Create `WorkOrderResponse` (with customer, site, assignee, and SLA details).

#### Task 7.4: Work-Order Service Implementation
- [x] **Task 7.4.1**: `createWorkOrder(CreateWorkOrderRequest req)`:
  - Validates customer and site exist, and site belongs to customer.
  - Generates unique code and SLA due date.
  - Sets initial status to `NEW` (or `ASSIGNED` if assignee present).
- [x] **Task 7.4.2**: `getWorkOrderById(Long id)`: Returns work order details.
- [x] **Task 7.4.3**: `updateWorkOrder(Long id, UpdateWorkOrderRequest req)`:
  - Enforces immutability rule: rejects updates if status is `CLOSED` or `CANCELLED`.

#### Task 7.5: Work-Order REST Controller
- [x] **Task 7.5.1**: Create `WorkOrderController`:
  - `POST /api/work-orders` (`201 Created`)
  - `GET /api/work-orders/{id}` (`200 OK`)
  - `PUT /api/work-orders/{id}` (`200 OK`)
- [x] **Task 7.5.2**: Update `SecurityConfig` to configure permissions for `/api/work-orders/**`.

---

### 🔹 Day 9: Work-Order Kanban Board in React SPA

#### Task 9.1: Frontend API Client
- [x] **Task 9.1.1**: Create API client utility in `frontend/src/api/apiClient.js` with auth header injection.

#### Task 9.2: Kanban Board Components
- [x] **Task 9.2.1**: Board columns for each status (`NEW`, `ASSIGNED`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `CLOSED`, `CANCELLED`).
- [x] **Task 9.2.2**: Work-order card displaying code, title, customer/site, priority color pill, and SLA countdown.
- [x] **Task 9.2.3**: Interactive status transition actions & creation modal with dynamic customer-site dropdowns.

---

### 🔹 Day 10: Search, Filtering, and Pagination (Review M2)

#### Task 10.1: Backend Filtering & Pagination
- [x] **Task 10.1.1**: Multi-parameter search in `WorkOrderRepository` and `WorkOrderService` (`GET /api/work-orders?page=0&size=10&status=...&priority=...&search=...`).
- [x] **Task 10.1.2**: Pagination with sorting by `slaDueAt ASC` as default.

#### Task 10.2: Centralized Error Handling (`@ControllerAdvice`)
- [x] **Task 10.2.1**: Create `GlobalExceptionHandler` to format Bean validation, access denied, and resource not found errors into uniform JSON responses.

#### Task 10.3: HTTP Testing Suite
- [x] **Task 10.3.1**: Add complete test cases to `request.http` for Customer, Site, Work-Order CRUD, and search/pagination.

#### Task 10.4: Milestone M2 Verification
- [x] **Task 10.4.1**: Backend build verification with `./mvnw test-compile` (52 source files compiled).
- [x] **Task 10.4.2**: Frontend build verification with `npm run build` (Production bundle built cleanly in 351ms).
- [x] **Task 10.4.3**: All F2 and F3 acceptance criteria satisfied.
