package com.KEYSTONE.dto;

import com.KEYSTONE.model.ServiceRequest;
import com.KEYSTONE.model.WorkOrder;

import java.time.LocalDateTime;

public class ServiceRequestResponse {

    private Long id;
    private String code;
    private Long customerId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long assignedTechnicianId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceRequestResponse() {
    }

    public ServiceRequestResponse(Long id, Long customerId, String title, String description,
                                  String status, String priority, Long assignedTechnicianId,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedTechnicianId = assignedTechnicianId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceRequestResponse fromEntity(ServiceRequest request) {
        ServiceRequestResponse res = new ServiceRequestResponse(
                request.getId(),
                request.getCustomerId(),
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getPriority(),
                request.getAssignedTechnicianId(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
        return res;
    }

    public static ServiceRequestResponse fromWorkOrder(WorkOrder wo) {
        ServiceRequestResponse res = new ServiceRequestResponse();
        res.setId(wo.getId());
        res.setCode(wo.getCode());
        res.setCustomerId(wo.getCustomer() != null ? wo.getCustomer().getId() : null);
        res.setTitle(wo.getTitle());
        res.setDescription(wo.getDescription());
        res.setStatus(wo.getStatus() != null ? wo.getStatus().name() : "NEW");
        res.setPriority(wo.getPriority());
        res.setAssignedTechnicianId(wo.getAssignee() != null ? wo.getAssignee().getId() : null);
        res.setCreatedAt(LocalDateTime.now());
        res.setUpdatedAt(LocalDateTime.now());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public void setAssignedTechnicianId(Long assignedTechnicianId) {
        this.assignedTechnicianId = assignedTechnicianId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
