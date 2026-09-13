package com.KEYSTONE.service;

import com.KEYSTONE.dto.*;
import com.KEYSTONE.model.*;
import com.KEYSTONE.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final WorkOrderStatusHistoryRepository historyRepository;
    private final PartRepository partRepository;
    private final PartUsageRepository partUsageRepository;
    private final TimeLogRepository timeLogRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository,
                            CustomerRepository customerRepository,
                            SiteRepository siteRepository,
                            UserRepository userRepository,
                            WorkOrderStatusHistoryRepository historyRepository,
                            PartRepository partRepository,
                            PartUsageRepository partUsageRepository,
                            TimeLogRepository timeLogRepository) {
        this.workOrderRepository = workOrderRepository;
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.partRepository = partRepository;
        this.partUsageRepository = partUsageRepository;
        this.timeLogRepository = timeLogRepository;
    }

    @Transactional
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request, String creatorEmail) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + request.getCustomerId()));

        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("Site not found with ID: " + request.getSiteId()));

        if (!site.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Site with ID " + site.getId() + " does not belong to Customer " + customer.getId());
        }

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee user not found with ID: " + request.getAssigneeId()));
            if (assignee.getRole() != Role.TECHNICIAN) {
                throw new IllegalArgumentException("Assignee must be a TECHNICIAN");
            }
        }

        User creator = creatorEmail != null ? userRepository.findByEmail(creatorEmail).orElse(null) : null;

        String priority = request.getPriority() != null ? request.getPriority().trim().toUpperCase() : "MEDIUM";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slaDueAt = calculateSlaDueAt(now, priority);
        String generatedCode = generateNextWorkOrderCode();

        WorkOrderStatus initialStatus = assignee != null ? WorkOrderStatus.ASSIGNED : WorkOrderStatus.NEW;

        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode(generatedCode);
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(priority);
        workOrder.setStatus(initialStatus);
        workOrder.setSlaDueAt(slaDueAt);
        workOrder.setCustomer(customer);
        workOrder.setSite(site);
        workOrder.setAssignee(assignee);

        WorkOrder saved = workOrderRepository.save(workOrder);

        // Record initial status in audit history
        recordHistory(saved, initialStatus, creator, "Work order created and queued as " + initialStatus);

        return enrichWithDetails(saved);
    }

    public WorkOrderResponse getWorkOrderById(Long id) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + id));
        return enrichWithDetails(workOrder);
    }

    @Transactional
    public WorkOrderResponse updateWorkOrder(Long id, UpdateWorkOrderRequest request) {
        WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + id));

        if (workOrder.getStatus() == WorkOrderStatus.CLOSED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify a closed or cancelled work order");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            workOrder.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workOrder.setDescription(request.getDescription());
        }
        if (request.getPriority() != null && !request.getPriority().isBlank()) {
            String newPriority = request.getPriority().trim().toUpperCase();
            workOrder.setPriority(newPriority);
            workOrder.setSlaDueAt(calculateSlaDueAt(LocalDateTime.now(), newPriority));
        }
        if (request.getSiteId() != null) {
            Site site = siteRepository.findById(request.getSiteId())
                    .orElseThrow(() -> new IllegalArgumentException("Site not found with ID: " + request.getSiteId()));
            if (!site.getCustomer().getId().equals(workOrder.getCustomer().getId())) {
                throw new IllegalArgumentException("Site does not belong to the work order's customer");
            }
            workOrder.setSite(site);
        }

        WorkOrder updated = workOrderRepository.save(workOrder);
        return enrichWithDetails(updated);
    }

    // --- State Machine Transitions ---

    @Transactional
    public WorkOrderResponse assignTechnician(Long workOrderId, Long technicianId, String dispatcherEmail) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        if (workOrder.getStatus() == WorkOrderStatus.CLOSED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot assign a closed or cancelled work order");
        }

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with ID: " + technicianId));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException("User with ID " + technicianId + " is not a technician");
        }

        User dispatcher = userRepository.findByEmail(dispatcherEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dispatcherEmail));

        workOrder.setAssignee(technician);
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);

        WorkOrder saved = workOrderRepository.save(workOrder);
        recordHistory(saved, WorkOrderStatus.ASSIGNED, dispatcher, "Assigned to technician: " + technician.getEmail());

        return enrichWithDetails(saved);
    }

    @Transactional
    public WorkOrderResponse transitionStatus(Long workOrderId, String targetStatusStr, String note, String userEmail) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        WorkOrderStatus currentStatus = workOrder.getStatus();
        WorkOrderStatus targetStatus;
        try {
            targetStatus = WorkOrderStatus.valueOf(targetStatusStr.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid work order status: " + targetStatusStr);
        }

        // Terminal state check
        if (currentStatus == WorkOrderStatus.CLOSED || currentStatus == WorkOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot transition a terminal work order in state " + currentStatus);
        }

        // Governed state machine validation
        validateStateTransition(currentStatus, targetStatus, user, workOrder);

        workOrder.setStatus(targetStatus);
        WorkOrder saved = workOrderRepository.save(workOrder);

        recordHistory(saved, targetStatus, user, note);

        return enrichWithDetails(saved);
    }

    private void validateStateTransition(WorkOrderStatus current, WorkOrderStatus target, User user, WorkOrder order) {
        Role role = user.getRole();

        // Cancellation (Allowed for Dispatcher & Manager from open states)
        if (target == WorkOrderStatus.CANCELLED) {
            if (role != Role.DISPATCHER && role != Role.MANAGER) {
                throw new AccessDeniedException("Only Dispatchers or Managers can cancel work orders");
            }
            if (current != WorkOrderStatus.NEW && current != WorkOrderStatus.OPEN && current != WorkOrderStatus.ASSIGNED && current != WorkOrderStatus.ON_HOLD) {
                throw new IllegalStateException("Cannot cancel a work order from state " + current);
            }
            return;
        }

        // Close-out (Strictly Manager Only)
        if (target == WorkOrderStatus.CLOSED) {
            if (role != Role.MANAGER) {
                throw new AccessDeniedException("Only a Manager can close and sign off on a work order");
            }
            if (current != WorkOrderStatus.COMPLETED) {
                throw new IllegalStateException("Work order must be COMPLETED before it can be CLOSED");
            }
            return;
        }

        // Technician Execution Transitions
        if (target == WorkOrderStatus.IN_PROGRESS) {
            if (current != WorkOrderStatus.ASSIGNED && current != WorkOrderStatus.ON_HOLD) {
                throw new IllegalStateException("Cannot start work from status " + current);
            }
            verifyTechnicianOwnership(order, user);
            return;
        }

        if (target == WorkOrderStatus.ON_HOLD) {
            if (current != WorkOrderStatus.IN_PROGRESS) {
                throw new IllegalStateException("Can only place an IN_PROGRESS work order ON_HOLD");
            }
            verifyTechnicianOwnership(order, user);
            return;
        }

        if (target == WorkOrderStatus.COMPLETED) {
            if (current != WorkOrderStatus.IN_PROGRESS) {
                throw new IllegalStateException("Can only mark an IN_PROGRESS work order as COMPLETED");
            }
            verifyTechnicianOwnership(order, user);
            return;
        }

        if (target == WorkOrderStatus.ASSIGNED) {
            if (role != Role.DISPATCHER && role != Role.MANAGER) {
                throw new AccessDeniedException("Only Dispatchers or Managers can assign work orders");
            }
            return;
        }

        throw new IllegalStateException("Illegal state transition from " + current + " to " + target);
    }

    private void verifyTechnicianOwnership(WorkOrder order, User user) {
        if (user.getRole() == Role.MANAGER) {
            return; // Managers have override authority
        }
        if (order.getAssignee() == null || !order.getAssignee().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not the assigned technician for this work order");
        }
    }

    // --- Transactional Parts Logging ---

    @Transactional
    public PartUsageResponse logPartUsage(Long workOrderId, LogPartUsageRequest request, String userEmail) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        if (workOrder.getStatus() != WorkOrderStatus.IN_PROGRESS && workOrder.getStatus() != WorkOrderStatus.ASSIGNED) {
            throw new IllegalStateException("Can only log parts against an active work order");
        }

        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + request.getPartId()));

        // Invariant: Stock cannot go negative
        if (part.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient inventory stock for '" + part.getName() + "'. Available: " +
                            part.getStockQuantity() + ", requested: " + request.getQuantity()
            );
        }

        // Atomically decrement stock
        part.setStockQuantity(part.getStockQuantity() - request.getQuantity());
        partRepository.save(part);

        // Record Part Usage
        PartUsage partUsage = new PartUsage();
        partUsage.setWorkOrder(workOrder);
        partUsage.setPart(part);
        partUsage.setQuantity(request.getQuantity());
        partUsage.setUnitCost(part.getUnitCost());

        PartUsage saved = partUsageRepository.save(partUsage);
        return PartUsageResponse.fromEntity(saved);
    }

    // --- Time Logging ---

    @Transactional
    public TimeLogResponse logTime(Long workOrderId, LogTimeRequest request, String technicianEmail) {
        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + workOrderId));

        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        TimeLog timeLog = new TimeLog();
        timeLog.setWorkOrder(workOrder);
        timeLog.setTechnician(technician);
        timeLog.setMinutes(request.getMinutes());
        timeLog.setNote(request.getNote());

        TimeLog saved = timeLogRepository.save(timeLog);
        return TimeLogResponse.fromEntity(saved);
    }

    // --- History & Reference Queries ---

    public List<WorkOrderStatusHistoryResponse> getStatusHistory(Long workOrderId) {
        return historyRepository.findByWorkOrderIdOrderByChangedAtAsc(workOrderId)
                .stream()
                .map(WorkOrderStatusHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PartUsageResponse> getPartsUsed(Long workOrderId) {
        return partUsageRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(PartUsageResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TimeLogResponse> getTimeLogs(Long workOrderId) {
        return timeLogRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(TimeLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PartResponse> getAllParts() {
        return partRepository.findAll()
                .stream()
                .map(PartResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<WorkOrderResponse> getTechnicianWorkOrders(String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        return workOrderRepository.findByAssigneeId(technician.getId())
                .stream()
                .map(this::enrichWithDetails)
                .collect(Collectors.toList());
    }

    // --- Multi-Criteria Search & Pagination ---

    public Page<WorkOrderResponse> searchWorkOrders(
            String status,
            String priority,
            Long customerId,
            Long siteId,
            Long assigneeId,
            String search,
            Pageable pageable) {

        Specification<WorkOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), WorkOrderStatus.valueOf(status.trim().toUpperCase())));
            }
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(root.get("priority"), priority.trim().toUpperCase()));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (siteId != null) {
                predicates.add(cb.equal(root.get("site").get("id"), siteId));
            }
            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate codeMatch = cb.like(cb.lower(root.get("code")), searchPattern);
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(codeMatch, titleMatch, descMatch));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return workOrderRepository.findAll(spec, pageable)
                .map(this::enrichWithDetails);
    }

    // --- Helper Methods ---

    private void recordHistory(WorkOrder order, WorkOrderStatus status, User changedBy, String note) {
        WorkOrderStatusHistory history = new WorkOrderStatusHistory(
                order,
                status,
                changedBy,
                LocalDateTime.now(),
                note
        );
        historyRepository.save(history);
    }

    private WorkOrderResponse enrichWithDetails(WorkOrder order) {
        WorkOrderResponse res = WorkOrderResponse.fromEntity(order);

        List<PartUsage> parts = partUsageRepository.findByWorkOrderId(order.getId());
        List<TimeLog> timeLogs = timeLogRepository.findByWorkOrderId(order.getId());
        List<WorkOrderStatusHistory> history = historyRepository.findByWorkOrderIdOrderByChangedAtAsc(order.getId());

        BigDecimal totalPartsCost = parts.stream()
                .map(p -> p.getUnitCost().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalLaborMinutes = timeLogs.stream()
                .mapToInt(TimeLog::getMinutes)
                .sum();

        res.setTotalPartsCost(totalPartsCost);
        res.setTotalLaborMinutes(totalLaborMinutes);
        res.setPartsUsed(parts.stream().map(PartUsageResponse::fromEntity).collect(Collectors.toList()));
        res.setTimeLogs(timeLogs.stream().map(TimeLogResponse::fromEntity).collect(Collectors.toList()));
        res.setStatusHistory(history.stream().map(WorkOrderStatusHistoryResponse::fromEntity).collect(Collectors.toList()));

        return res;
    }

    private LocalDateTime calculateSlaDueAt(LocalDateTime start, String priority) {
        return switch (priority.toUpperCase()) {
            case "HIGH" -> start.plusHours(4);
            case "LOW" -> start.plusHours(48);
            default -> start.plusHours(24); // MEDIUM
        };
    }

    private synchronized String generateNextWorkOrderCode() {
        int currentYear = Year.now().getValue();
        String prefix = "WO-" + currentYear + "-";
        long count = workOrderRepository.countByCodePrefix(prefix);
        return String.format("%s%04d", prefix, count + 1);
    }
}
