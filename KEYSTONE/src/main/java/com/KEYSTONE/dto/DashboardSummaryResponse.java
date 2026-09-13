package com.KEYSTONE.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardSummaryResponse {

    private long totalWorkOrders;
    private long activeWorkOrders;
    private long overdueWorkOrders;
    private double slaComplianceRate; // Percentage e.g. 88.5
    private Map<String, Long> statusCounts = new HashMap<>();
    private List<TechnicianPerformanceDto> technicianBreakdown;
    private List<SiteWorkloadDto> siteBreakdown;

    public DashboardSummaryResponse() {
    }

    public long getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(long totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public long getActiveWorkOrders() {
        return activeWorkOrders;
    }

    public void setActiveWorkOrders(long activeWorkOrders) {
        this.activeWorkOrders = activeWorkOrders;
    }

    public long getOverdueWorkOrders() {
        return overdueWorkOrders;
    }

    public void setOverdueWorkOrders(long overdueWorkOrders) {
        this.overdueWorkOrders = overdueWorkOrders;
    }

    public double getSlaComplianceRate() {
        return slaComplianceRate;
    }

    public void setSlaComplianceRate(double slaComplianceRate) {
        this.slaComplianceRate = slaComplianceRate;
    }

    public Map<String, Long> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(Map<String, Long> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public List<TechnicianPerformanceDto> getTechnicianBreakdown() {
        return technicianBreakdown;
    }

    public void setTechnicianBreakdown(List<TechnicianPerformanceDto> technicianBreakdown) {
        this.technicianBreakdown = technicianBreakdown;
    }

    public List<SiteWorkloadDto> getSiteBreakdown() {
        return siteBreakdown;
    }

    public void setSiteBreakdown(List<SiteWorkloadDto> siteBreakdown) {
        this.siteBreakdown = siteBreakdown;
    }
}
