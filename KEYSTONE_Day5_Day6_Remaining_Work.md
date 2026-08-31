# KEYSTONE — Remaining Work for Day 5 and Day 6

## Current Progress

### Completed before Day 5
- PostgreSQL database setup
- Flyway migrations through V12
- User model and seeded users
- UserRepository
- Spring Security configuration
- CORS configuration
- BCrypt password hashing
- LoginRequest and LoginResponse DTOs
- AuthService
- AuthController
- JWT generation and validation
- JwtAuthenticationFilter
- Protected endpoint testing
- Role-Based Access Control (RBAC)

### Day 5 completed so far
- ServiceRequest entity created
- `service_requests` PostgreSQL table created with Flyway V13
- ServiceRequestRepository created
- CreateServiceRequestRequest DTO created
- ServiceRequestResponse DTO created

---

# DAY 5 — Remaining Work

## Step 6 — Create ServiceRequestService

Create:

`service/ServiceRequestService.java`

Responsibilities:

- Receive the authenticated customer
- Receive title, description, and priority
- Create a new ServiceRequest
- Set default status
- Set `createdAt` and `updatedAt`
- Save using ServiceRequestRepository
- Return ServiceRequestResponse

---

## Step 7 — Get the Authenticated User

Connect the JWT/Spring Security context to the current user.

Goal:

`JWT → SecurityContext → current user email → user ID`

This prevents a customer from manually sending another customer's ID.

---

## Step 8 — Create Customer Service Request API

Create:

`POST /api/customer/requests`

Flow:

`Customer JWT`
→ `JWT Filter`
→ `Customer role verified`
→ `Customer identity obtained`
→ `ServiceRequest created`
→ `Saved in PostgreSQL`
→ `Response returned`

Test using a CUSTOMER JWT.

---

## Step 9 — Validate Request Creation

Test:

- Valid customer token + valid request → `201 Created`
- No token → blocked
- Wrong role → `403 Forbidden`
- Verify request exists in PostgreSQL

Example:

```json
{
  "title": "Air conditioner not working",
  "description": "The AC is not cooling properly.",
  "priority": "HIGH"
}
```

---

## Step 10 — Customer View Requests

Create:

`GET /api/customer/requests`

The customer should only see requests belonging to them.

---

# DAY 5 COMPLETION GOAL

`Customer logs in`
→ `JWT`
→ `Customer creates service request`
→ `Request stored in database`
→ `Customer views own requests`

---

# DAY 6 — Dispatcher and Technician Workflow

## Step 1 — Dispatcher View All Service Requests

Create:

`GET /api/dispatcher/requests`

Dispatcher can view all customer requests.

Possible statuses:

* PENDING
* ASSIGNED
* IN_PROGRESS
* COMPLETED
* CANCELLED

---

## Step 2 — Dispatcher View Unassigned Requests

Create:

`GET /api/dispatcher/requests/unassigned`

This shows requests that still need a technician.

---

## Step 3 — Technician Listing

Create functionality allowing the dispatcher to view available technicians.

Example:

`GET /api/dispatcher/technicians`

---

## Step 4 — Assign Technician to Service Request

Create:

`PUT /api/dispatcher/requests/{requestId}/assign`

Flow:

`Dispatcher`
→ Select service request
→ Select technician
→ Update `assignedTechnicianId`
→ Change status from `PENDING` to `ASSIGNED`

---

## Step 5 — Technician View Assigned Requests

Create:

`GET /api/technician/requests`

A technician should only see requests assigned to them.

---

## Step 6 — Technician Update Request Status

Create:

`PUT /api/technician/requests/{requestId}/status`

Possible transitions:

* `ASSIGNED → IN_PROGRESS`
* `IN_PROGRESS → COMPLETED`

A technician must not be able to update another technician's request.

---

## Step 7 — Authorization Testing

| User       | Action                              | Expected  |
| ---------- | ----------------------------------- | --------- |
| Customer   | Create request                      | Allowed   |
| Customer   | View own requests                   | Allowed   |
| Customer   | View all requests                   | Forbidden |
| Dispatcher | View all requests                   | Allowed   |
| Dispatcher | Assign technician                   | Allowed   |
| Technician | View assigned requests              | Allowed   |
| Technician | Update own assigned request         | Allowed   |
| Technician | Update another technician's request | Forbidden |

---

# DAY 6 COMPLETION GOAL

`CUSTOMER`
→ Creates request
→ `PENDING`

`DISPATCHER`
→ Views request
→ Assigns technician
→ `ASSIGNED`

`TECHNICIAN`
→ Views assigned request
→ Starts work
→ `IN_PROGRESS`
→ Completes work
→ `COMPLETED`

---

# Overall Status After Day 6

## Completed

* Authentication
* JWT
* BCrypt
* Protected APIs
* RBAC
* Customer request creation
* Customer request viewing
* Dispatcher request management
* Technician assignment
* Technician request workflow
* Service request status updates

## Remaining for Later

* Manager dashboard and analytics
* Better validation and error handling
* Proper enum-based statuses and priorities
* Global exception handling
* Pagination, filtering and search
* Frontend integration
* Production-ready JWT secret configuration
* Logging and testing
* Deployment
