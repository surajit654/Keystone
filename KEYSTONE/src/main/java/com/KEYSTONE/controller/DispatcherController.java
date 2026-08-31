package com.KEYSTONE.controller;

import com.KEYSTONE.dto.AssignTechnicianRequest;
import com.KEYSTONE.dto.ServiceRequestResponse;
import com.KEYSTONE.dto.TechnicianResponse;
import com.KEYSTONE.service.ServiceRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatcher")
public class DispatcherController {

    private final ServiceRequestService serviceRequestService;

    public DispatcherController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(serviceRequestService.getAllRequests());
    }

    @GetMapping("/requests/unassigned")
    public ResponseEntity<List<ServiceRequestResponse>> getUnassignedRequests() {
        return ResponseEntity.ok(serviceRequestService.getUnassignedRequests());
    }

    @GetMapping("/technicians")
    public ResponseEntity<List<TechnicianResponse>> getAvailableTechnicians() {
        return ResponseEntity.ok(serviceRequestService.getAvailableTechnicians());
    }

    @PutMapping("/requests/{requestId}/assign")
    public ResponseEntity<ServiceRequestResponse> assignTechnician(
            @PathVariable Long requestId,
            @RequestBody AssignTechnicianRequest request) {
        return ResponseEntity.ok(serviceRequestService.assignTechnician(requestId, request.getTechnicianId()));
    }
}
