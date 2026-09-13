package com.KEYSTONE.dto;

import com.KEYSTONE.model.WorkOrder;
import com.KEYSTONE.model.WorkOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderResponse {

    private Long id;
    private String code;
    private String title;
    private String description;
    private String priority;
    private WorkOrderStatus status;
    private LocalDateTime slaDueAt;
    private boolean slaBreached;

    private Long customerId;
    private String customerName;

    private Long siteId;
    private String siteName;
    private String siteAddress;
    private String siteCity;

    private Long assigneeId;
    private String assigneeEmail;

    private BigDecimal totalPartsCost = BigDecimal.ZERO;
    private Integer totalLaborMinutes = 0;

    private List<PartUsageResponse> partsUsed = new ArrayList<>();
    private List<TimeLogResponse> timeLogs = new ArrayList<>();
    private List<WorkOrderStatusHistoryResponse> statusHistory = new ArrayList<>();

    public WorkOrderResponse() {
    }

    public static WorkOrderResponse fromEntity(WorkOrder order) {
        WorkOrderResponse res = new WorkOrderResponse();
        res.setId(order.getId());
        res.setCode(order.getCode());
        res.setTitle(order.getTitle());
        res.setDescription(order.getDescription());
        res.setPriority(order.getPriority());
        res.setStatus(order.getStatus());
        res.setSlaDueAt(order.getSlaDueAt());
        res.setSlaBreached(order.getSlaDueAt() != null && LocalDateTime.now().isAfter(order.getSlaDueAt())
                && order.getStatus() != WorkOrderStatus.COMPLETED
                && order.getStatus() != WorkOrderStatus.CLOSED
                && order.getStatus() != WorkOrderStatus.CANCELLED);

        if (order.getCustomer() != null) {
            res.setCustomerId(order.getCustomer().getId());
            res.setCustomerName(order.getCustomer().getName());
        }

        if (order.getSite() != null) {
            res.setSiteId(order.getSite().getId());
            res.setSiteName(order.getSite().getName());
            res.setSiteAddress(order.getSite().getAddress());
            res.setSiteCity(order.getSite().getCity());
        }

        if (order.getAssignee() != null) {
            res.setAssigneeId(order.getAssignee().getId());
            res.setAssigneeEmail(order.getAssignee().getEmail());
        }

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

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getSlaDueAt() {
        return slaDueAt;
    }

    public void setSlaDueAt(LocalDateTime slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    public boolean isSlaBreached() {
        return slaBreached;
    }

    public void setSlaBreached(boolean slaBreached) {
        this.slaBreached = slaBreached;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getSiteCity() {
        return siteCity;
    }

    public void setSiteCity(String siteCity) {
        this.siteCity = siteCity;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }

    public BigDecimal getTotalPartsCost() {
        return totalPartsCost;
    }

    public void setTotalPartsCost(BigDecimal totalPartsCost) {
        this.totalPartsCost = totalPartsCost;
    }

    public Integer getTotalLaborMinutes() {
        return totalLaborMinutes;
    }

    public void setTotalLaborMinutes(Integer totalLaborMinutes) {
        this.totalLaborMinutes = totalLaborMinutes;
    }

    public List<PartUsageResponse> getPartsUsed() {
        return partsUsed;
    }

    public void setPartsUsed(List<PartUsageResponse> partsUsed) {
        this.partsUsed = partsUsed;
    }

    public List<TimeLogResponse> getTimeLogs() {
        return timeLogs;
    }

    public void setTimeLogs(List<TimeLogResponse> timeLogs) {
        this.timeLogs = timeLogs;
    }

    public List<WorkOrderStatusHistoryResponse> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<WorkOrderStatusHistoryResponse> statusHistory) {
        this.statusHistory = statusHistory;
    }
}
