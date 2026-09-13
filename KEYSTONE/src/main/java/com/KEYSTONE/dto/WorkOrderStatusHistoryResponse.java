package com.KEYSTONE.dto;

import com.KEYSTONE.model.WorkOrderStatus;
import com.KEYSTONE.model.WorkOrderStatusHistory;

import java.time.LocalDateTime;

public class WorkOrderStatusHistoryResponse {

    private Long id;
    private Long workOrderId;
    private WorkOrderStatus status;
    private String changedByEmail;
    private LocalDateTime changedAt;
    private String note;

    public WorkOrderStatusHistoryResponse() {
    }

    public static WorkOrderStatusHistoryResponse fromEntity(WorkOrderStatusHistory history) {
        WorkOrderStatusHistoryResponse res = new WorkOrderStatusHistoryResponse();
        res.setId(history.getId());
        res.setWorkOrderId(history.getWorkOrder() != null ? history.getWorkOrder().getId() : null);
        res.setStatus(history.getStatus());
        res.setChangedByEmail(history.getChangedBy() != null ? history.getChangedBy().getEmail() : null);
        res.setChangedAt(history.getChangedAt());
        res.setNote(history.getNote());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public String getChangedByEmail() {
        return changedByEmail;
    }

    public void setChangedByEmail(String changedByEmail) {
        this.changedByEmail = changedByEmail;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
