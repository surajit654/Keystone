package com.KEYSTONE.controller;

import com.KEYSTONE.dto.ServiceRequestResponse;
import com.KEYSTONE.dto.UpdateStatusRequest;
import com.KEYSTONE.service.ServiceRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technician")
public class TechnicianController {

    private final ServiceRequestService serviceRequestService;

    public TechnicianController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<ServiceRequestResponse>> getAssignedRequests(
            Authentication authentication) {
        return ResponseEntity.ok(serviceRequestService.getTechnicianAssignedRequests(authentication.getName()));
    }

    @PutMapping("/requests/{requestId}/status")
    public ResponseEntity<ServiceRequestResponse> updateStatus(
            @PathVariable Long requestId,
            @RequestBody UpdateStatusRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(serviceRequestService.updateTechnicianRequestStatus(
                requestId,
                request.getStatus(),
                authentication.getName()
        ));
    }
}
