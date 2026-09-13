package com.KEYSTONE.controller;

import com.KEYSTONE.dto.*;
import com.KEYSTONE.service.WorkOrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    // --- Work Order CRUD ---

    @PostMapping("/api/work-orders")
    public ResponseEntity<WorkOrderResponse> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderRequest request,
            Authentication authentication) {
        WorkOrderResponse response = workOrderService.createWorkOrder(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/work-orders/{id}")
    public ResponseEntity<WorkOrderResponse> getWorkOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }

    @PutMapping("/api/work-orders/{id}")
    public ResponseEntity<WorkOrderResponse> updateWorkOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkOrderRequest request) {
        return ResponseEntity.ok(workOrderService.updateWorkOrder(id, request));
    }

    @GetMapping("/api/work-orders")
    public ResponseEntity<Page<WorkOrderResponse>> searchWorkOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "slaDueAt", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(workOrderService.searchWorkOrders(
                status, priority, customerId, siteId, assigneeId, search, pageable
        ));
    }

    // --- Dispatch & Assignment ---

    @PostMapping("/api/work-orders/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assignTechnicianPost(
            @PathVariable Long id,
            @RequestBody AssignTechnicianRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(workOrderService.assignTechnician(id, request.getTechnicianId(), authentication.getName()));
    }

    @PutMapping("/api/work-orders/{id}/assign")
    public ResponseEntity<WorkOrderResponse> assignTechnicianPut(
            @PathVariable Long id,
            @RequestBody AssignTechnicianRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(workOrderService.assignTechnician(id, request.getTechnicianId(), authentication.getName()));
    }

    // --- State Machine Transitions ---

    @PostMapping("/api/work-orders/{id}/status")
    public ResponseEntity<WorkOrderResponse> transitionStatus(
            @PathVariable Long id,
            @Valid @RequestBody TransitionStatusRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(workOrderService.transitionStatus(id, request.getStatus(), request.getNote(), authentication.getName()));
    }

    @GetMapping("/api/work-orders/{id}/history")
    public ResponseEntity<List<WorkOrderStatusHistoryResponse>> getStatusHistory(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getStatusHistory(id));
    }

    // --- Parts Usage ---

    @PostMapping("/api/work-orders/{id}/parts")
    public ResponseEntity<PartUsageResponse> logPartUsage(
            @PathVariable Long id,
            @Valid @RequestBody LogPartUsageRequest request,
            Authentication authentication) {
        PartUsageResponse response = workOrderService.logPartUsage(id, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/work-orders/{id}/parts")
    public ResponseEntity<List<PartUsageResponse>> getPartsUsed(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getPartsUsed(id));
    }

    @GetMapping("/api/parts")
    public ResponseEntity<List<PartResponse>> getAllParts() {
        return ResponseEntity.ok(workOrderService.getAllParts());
    }

    // --- Time Logging ---

    @PostMapping("/api/work-orders/{id}/time")
    public ResponseEntity<TimeLogResponse> logTime(
            @PathVariable Long id,
            @Valid @RequestBody LogTimeRequest request,
            Authentication authentication) {
        TimeLogResponse response = workOrderService.logTime(id, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/work-orders/{id}/time")
    public ResponseEntity<List<TimeLogResponse>> getTimeLogs(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getTimeLogs(id));
    }

    // --- Technician Dedicated Endpoint ---

    @GetMapping("/api/technician/work-orders")
    public ResponseEntity<List<WorkOrderResponse>> getTechnicianWorkOrders(Authentication authentication) {
        return ResponseEntity.ok(workOrderService.getTechnicianWorkOrders(authentication.getName()));
    }
}
