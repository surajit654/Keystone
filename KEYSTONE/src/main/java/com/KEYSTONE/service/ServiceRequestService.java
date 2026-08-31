package com.KEYSTONE.service;

import com.KEYSTONE.dto.CreateServiceRequestRequest;
import com.KEYSTONE.dto.ServiceRequestResponse;
import com.KEYSTONE.dto.TechnicianResponse;
import com.KEYSTONE.model.Role;
import com.KEYSTONE.model.ServiceRequest;
import com.KEYSTONE.model.User;
import com.KEYSTONE.repository.ServiceRequestRepository;
import com.KEYSTONE.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository,
                                 UserRepository userRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
    }

    // --- Customer Operations ---

    public ServiceRequestResponse createCustomerRequest(CreateServiceRequestRequest request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + customerEmail));

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setCustomerId(customer.getId());
        serviceRequest.setTitle(request.getTitle());
        serviceRequest.setDescription(request.getDescription());
        serviceRequest.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        serviceRequest.setStatus("PENDING");
        serviceRequest.setAssignedTechnicianId(null);
        serviceRequest.setCreatedAt(LocalDateTime.now());
        serviceRequest.setUpdatedAt(LocalDateTime.now());

        ServiceRequest saved = serviceRequestRepository.save(serviceRequest);
        return ServiceRequestResponse.fromEntity(saved);
    }

    public List<ServiceRequestResponse> getCustomerRequests(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + customerEmail));

        return serviceRequestRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(ServiceRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Dispatcher Operations ---

    public List<ServiceRequestResponse> getAllRequests() {
        return serviceRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ServiceRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ServiceRequestResponse> getUnassignedRequests() {
        return serviceRequestRepository.findByAssignedTechnicianIdIsNullOrderByCreatedAtDesc()
                .stream()
                .map(ServiceRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TechnicianResponse> getAvailableTechnicians() {
        return userRepository.findByRole(Role.TECHNICIAN)
                .stream()
                .map(TechnicianResponse::fromUser)
                .collect(Collectors.toList());
    }

    public ServiceRequestResponse assignTechnician(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with ID: " + technicianId));

        if (technician.getRole() != Role.TECHNICIAN) {
            throw new IllegalArgumentException("User with ID " + technicianId + " is not a technician");
        }

        request.setAssignedTechnicianId(technicianId);
        request.setStatus("ASSIGNED");
        request.setUpdatedAt(LocalDateTime.now());

        ServiceRequest saved = serviceRequestRepository.save(request);
        return ServiceRequestResponse.fromEntity(saved);
    }

    // --- Technician Operations ---

    public List<ServiceRequestResponse> getTechnicianAssignedRequests(String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        return serviceRequestRepository.findByAssignedTechnicianIdOrderByCreatedAtDesc(technician.getId())
                .stream()
                .map(ServiceRequestResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ServiceRequestResponse updateTechnicianRequestStatus(Long requestId, String newStatus, String technicianEmail) {
        User technician = userRepository.findByEmail(technicianEmail)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found: " + technicianEmail));

        ServiceRequest request = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Service request not found with ID: " + requestId));

        if (request.getAssignedTechnicianId() == null || !request.getAssignedTechnicianId().equals(technician.getId())) {
            throw new AccessDeniedException("You are not authorized to update this service request");
        }

        String currentStatus = request.getStatus();
        String targetStatus = newStatus != null ? newStatus.trim().toUpperCase() : "";

        // Validate allowed transitions: ASSIGNED -> IN_PROGRESS -> COMPLETED
        boolean isValidTransition = ("ASSIGNED".equalsIgnoreCase(currentStatus) && "IN_PROGRESS".equalsIgnoreCase(targetStatus))
                || ("IN_PROGRESS".equalsIgnoreCase(currentStatus) && "COMPLETED".equalsIgnoreCase(targetStatus));

        if (!isValidTransition) {
            throw new IllegalArgumentException("Invalid status transition from " + currentStatus + " to " + targetStatus);
        }

        request.setStatus(targetStatus);
        request.setUpdatedAt(LocalDateTime.now());

        ServiceRequest saved = serviceRequestRepository.save(request);
        return ServiceRequestResponse.fromEntity(saved);
    }
}
