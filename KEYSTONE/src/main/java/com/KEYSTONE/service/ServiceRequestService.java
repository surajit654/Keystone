package com.KEYSTONE.service;

import com.KEYSTONE.dto.CreateServiceRequestRequest;
import com.KEYSTONE.dto.ServiceRequestResponse;
import com.KEYSTONE.dto.TechnicianResponse;
import com.KEYSTONE.model.*;
import com.KEYSTONE.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final WorkOrderStatusHistoryRepository historyRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 UserRepository userRepository,
                                 WorkOrderRepository workOrderRepository,
                                 CustomerRepository customerRepository,
                                 SiteRepository siteRepository,
                                 WorkOrderStatusHistoryRepository historyRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.workOrderRepository = workOrderRepository;
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.historyRepository = historyRepository;
    }

    // --- Customer Operations ---

    @Transactional
    public ServiceRequestResponse createCustomerRequest(CreateServiceRequestRequest request, String customerEmail) {
        User customerUser = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + customerEmail));

        // 1. Resolve Customer entity
        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseGet(() -> customerRepository.findAll().stream().findFirst()
                        .orElseGet(() -> customerRepository.save(new Customer("Customer Account", customerEmail, "555-0100"))));

        // 2. Resolve Site entity
        List<Site> sites = siteRepository.findByCustomerId(customer.getId());
        Site site;
        if (!sites.isEmpty()) {
            site = sites.get(0);
        } else {
            List<Site> allSites = siteRepository.findAll();
            if (!allSites.isEmpty()) {
                site = allSites.get(0);
            } else {
                site = siteRepository.save(new Site("Main Campus HQ", "100 Primary Way", "New York", customer));
            }
        }

        String priority = request.getPriority() != null ? request.getPriority().trim().toUpperCase() : "MEDIUM";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slaDueAt = calculateSlaDueAt(now, priority);
        String generatedCode = generateNextWorkOrderCode();

        // 3. Create WorkOrder so it immediately appears in Dispatcher's Kanban Board
        WorkOrder workOrder = new WorkOrder();
        workOrder.setCode(generatedCode);
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setPriority(priority);
        workOrder.setStatus(WorkOrderStatus.NEW);
        workOrder.setSlaDueAt(slaDueAt);
        workOrder.setCustomer(customer);
        workOrder.setSite(site);
        workOrder.setAssignee(null);

        WorkOrder savedWo = workOrderRepository.save(workOrder);

        // Record initial status in audit history
        WorkOrderStatusHistory history = new WorkOrderStatusHistory(
                savedWo,
                WorkOrderStatus.NEW,
                customerUser,
                now,
                "Ticket raised via Customer Self-Service Portal"
        );
        historyRepository.save(history);

        // 4. Save in ServiceRequest table for backward-compatibility
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomerId(customerUser.getId());
        serviceRequest.setTitle(request.getTitle());
        serviceRequest.setDescription(request.getDescription());
        serviceRequest.setPriority(priority);
        serviceRequest.setStatus("NEW");
        serviceRequest.setAssignedTechnicianId(null);
        serviceRequest.setCreatedAt(now);
        serviceRequest.setUpdatedAt(now);
        serviceRequestRepository.save(serviceRequest);

        ServiceRequestResponse response = ServiceRequestResponse.fromWorkOrder(savedWo);
        response.setCreatedAt(now);
        return response;
    }

    public List<ServiceRequestResponse> getCustomerRequests(String customerEmail) {
        User customerUser = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + customerEmail));

        Customer customer = customerRepository.findByEmail(customerEmail).orElse(null);
        List<WorkOrder> workOrders = new ArrayList<>();
        if (customer != null) {
            workOrders = workOrderRepository.findByCustomerId(customer.getId());
        } else {
            // Fallback to all work orders if single tenant demo
            workOrders = workOrderRepository.findAll();
        }

        return workOrders.stream()
                .map(ServiceRequestResponse::fromWorkOrder)
                .collect(Collectors.toList());
    }

    // --- Dispatcher Operations ---

    public List<ServiceRequestResponse> getAllRequests() {
        return workOrderRepository.findAll().stream()
                .map(ServiceRequestResponse::fromWorkOrder)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestResponse> getUnassignedRequests() {
        return workOrderRepository.findByStatus(WorkOrderStatus.NEW).stream()
                .map(ServiceRequestResponse::fromWorkOrder)
                .collect(Collectors.toList());
    }

    public List<TechnicianResponse> getAvailableTechnicians() {
        return userRepository.findByRole(Role.TECHNICIAN)
                .stream()
                .map(TechnicianResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceRequestResponse assignTechnician(Long requestId, Long technicianId) {
        WorkOrder workOrder = workOrderRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + requestId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with ID: " + technicianId));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException("User with ID " + technicianId + " is not a technician");
        }

        workOrder.setAssignee(technician);
        workOrder.setStatus(WorkOrderStatus.ASSIGNED);
        WorkOrder saved = workOrderRepository.save(workOrder);

        return ServiceRequestResponse.fromWorkOrder(saved);
    }

    // --- Technician Operations ---

    public List<ServiceRequestResponse> getTechnicianAssignedRequests(String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        return workOrderRepository.findByAssigneeId(technician.getId())
                .stream()
                .map(ServiceRequestResponse::fromWorkOrder)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceRequestResponse updateTechnicianRequestStatus(Long requestId, String newStatus, String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        WorkOrder workOrder = workOrderRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found with ID: " + requestId));

        if (workOrder.getAssignee() == null || !workOrder.getAssignee().getId().equals(technician.getId())) {
            throw new AccessDeniedException("You are not authorized to update this work order");
        }

        WorkOrderStatus targetStatus = WorkOrderStatus.valueOf(newStatus.trim().toUpperCase());
        workOrder.setStatus(targetStatus);
        WorkOrder saved = workOrderRepository.save(workOrder);

        return ServiceRequestResponse.fromWorkOrder(saved);
    }

    // --- Helper Methods ---

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
