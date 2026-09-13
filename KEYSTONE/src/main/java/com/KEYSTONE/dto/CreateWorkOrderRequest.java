package com.KEYSTONE.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateWorkOrderRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Priority is required (HIGH, MEDIUM, LOW)")
    private String priority;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Site ID is required")
    private Long siteId;

    private Long assigneeId;

    public CreateWorkOrderRequest() {
    }

    public CreateWorkOrderRequest(String title, String description, String priority, Long customerId, Long siteId, Long assigneeId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.customerId = customerId;
        this.siteId = siteId;
        this.assigneeId = assigneeId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
