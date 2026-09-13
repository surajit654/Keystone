# 🎬 Project KEYSTONE — Live Demo & Presentation Walkthrough Guide

This guide provides a structured **3 to 5 minute live demonstration script** showcasing the end-to-end capabilities of Project KEYSTONE across all 4 RBAC roles.

---

## 🎯 Demo Summary & Architecture Highlights

- **4 Distinct Personas**: Customer, Dispatcher, Technician, Manager.
- **Governed State Machine**: `NEW` ➔ `ASSIGNED` ➔ `IN_PROGRESS` ➔ `COMPLETED` ➔ `CLOSED`.
- **Real-Time SLAs**: Automated SLA target calculation based on priority.
- **Inventory & Labor**: Transactional parts decrement and technician time logging.
- **Executive Analytics**: Real-time KPI dashboard with SLA compliance and workload metrics.

---

## ⏱ Step-by-Step 5-Minute Demo Flow

### 👤 Phase 1: Customer Self-Service (1 min)
1. **Navigate to App**: Open `http://localhost:3000` (or `http://localhost:5173`).
2. **Login as Customer**:
   - Email: `customer@keystone.com`
   - Password: `password`
3. **Submit Urgent Service Ticket**:
   - Customer is redirected automatically to the **Customer Portal** (`/portal`).
   - Fill out the **New Service Request** form:
     - *Customer / Account*: Acme Corporation
     - *Site*: Acme HQ (Downtown Tower)
     - *Title*: "Main Server Room AC Unit Overheating"
     - *Priority*: `CRITICAL` (2-Hour SLA)
     - *Description*: "Server room temperature is climbing past 85F. Immediate technician required."
   - Click **Submit Service Request**.
4. **Observe**:
   - A success banner displays the newly generated tracking code (e.g. `WO-2026-0005`).
   - The ticket appears immediately in the **Live Request Tracker** with status badge `NEW`.
5. **Logout** (top right profile menu).

---

### 📋 Phase 2: Dispatcher Triage & Assignment (1 min)
1. **Login as Dispatcher**:
   - Email: `dispatcher@keystone.com`
   - Password: `password`
2. **Review Kanban Dispatch Board**:
   - Navigate to **Work Orders** (`/work-orders`).
   - Notice the 7-column Kanban board. The new ticket `WO-2026-0005` is located in the **NEW** column.
3. **Assign Technician**:
   - Click the **Assign** button on the ticket card.
   - Select `technician@keystone.com` from the dropdown list.
   - Click **Confirm Assignment**.
4. **Observe**:
   - Ticket card transitions smoothly into the **ASSIGNED** column.
   - An audit history record is created in the database tracking the assignment.
5. **Logout**.

---

### 🔧 Phase 3: Technician Field Execution & Logging (1.5 min)
1. **Login as Technician**:
   - Email: `technician@keystone.com`
   - Password: `password`
2. **View Mobile Field Workbench**:
   - Technician is redirected to the **Technician Field View** (`/technicians`).
   - See assigned jobs with SLA countdown timers.
3. **Start Job**:
   - On ticket `WO-2026-0005`, click **Start Work**.
   - Status updates to `IN_PROGRESS`.
4. **Log Inventory Parts Consumed**:
   - In the **Parts Used** drawer/modal:
     - Select part: `High Capacity HVAC Filter` (or `Compressor Belt`).
     - Enter quantity: `2`.
     - Click **Log Part**.
   - *Observe*: Stock is transactionally deducted from the warehouse inventory.
5. **Log Labor Time**:
   - Enter minutes: `75` (1h 15m).
   - Enter note: "Diagnosed blown thermal fuse, replaced filter, recharged coolant."
   - Click **Log Time**.
6. **Mark Job as Completed**:
   - Click **Mark Completed**.
   - Status transitions to `COMPLETED`.
7. **Logout**.

---

### 📊 Phase 4: Manager Executive Audit & Close-Out (1 min)
1. **Login as Manager**:
   - Email: `manager@keystone.com`
   - Password: `password`
2. **Inspect Executive Analytics Dashboard**:
   - Redirected to **Executive Dashboard** (`/dashboard`).
   - Review live KPIs:
     - *Total Work Orders* & *Active Jobs*
     - *SLA Compliance Rate %*
     - *Status Distribution Bar*
     - *Technician Workload & Performance Table*
     - *Site Workload Breakdown*
3. **Final Close-Out Sign-Off**:
   - Navigate to **Work Orders** (`/work-orders`).
   - Open ticket `WO-2026-0005` in the **COMPLETED** column.
   - Click **Review & Close Order**.
   - Enter closing note: "Customer verified server room temperature returned to 68F. Approved."
   - Click **Confirm Close-Out**.
4. **Observe**:
   - Work order transitions to terminal state **CLOSED**.
   - Total accumulated parts + labor costs are locked.

---

### 📑 Phase 5: OpenAPI / Swagger Verification (30 sec)
1. Open `http://localhost:8080/swagger-ui.html` in browser.
2. Demonstrate live interactive API documentation:
   - Expand `POST /api/auth/login` and show request/response schema.
   - Show `GET /api/reports/summary` and `GET /api/work-orders`.
   - Highlight JWT Bearer authorization integration.

---

## 🏆 Key Checklist Items Demonstrated

- [x] Multi-Role Security & JWT Authentication
- [x] Customer Self-Service Portal & Request Generation
- [x] Dispatcher Kanban & Technician Scheduling
- [x] Technician Mobile Workbench, Parts Consumption & Labor Logging
- [x] Governed State Machine & Audit History Trail
- [x] SLA Monitoring & Breached Triage
- [x] Executive KPI & Analytics Dashboard
- [x] Interactive OpenAPI 3.0 Documentation

---

© 2026 Project KEYSTONE — Meridian Facilities Management.
