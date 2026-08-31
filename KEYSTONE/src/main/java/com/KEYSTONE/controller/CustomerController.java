package com.KEYSTONE.controller;

import com.KEYSTONE.dto.CreateServiceRequestRequest;
import com.KEYSTONE.dto.ServiceRequestResponse;
import com.KEYSTONE.service.ServiceRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/requests")
public class CustomerController {

    private final ServiceRequestService serviceRequestService;

    public CustomerController(ServiceRequestService serviceRequestService) {
        this.serviceRequestService = serviceRequestService;
    }

    @PostMapping
    public ResponseEntity<ServiceRequestResponse> createRequest(
            @RequestBody CreateServiceRequestRequest request,
            Authentication authentication) {
        ServiceRequestResponse response = serviceRequestService.createCustomerRequest(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponse>> getMyRequests(
            Authentication authentication) {
        List<ServiceRequestResponse> responses = serviceRequestService.getCustomerRequests(authentication.getName());
        return ResponseEntity.ok(responses);
    }
}
