package com.KEYSTONE.service;

import com.KEYSTONE.dto.DashboardSummaryResponse;
import com.KEYSTONE.dto.SiteWorkloadDto;
import com.KEYSTONE.dto.TechnicianPerformanceDto;
import com.KEYSTONE.model.Role;
import com.KEYSTONE.model.User;
import com.KEYSTONE.model.WorkOrder;
import com.KEYSTONE.model.WorkOrderStatus;
import com.KEYSTONE.repository.SiteRepository;
import com.KEYSTONE.repository.UserRepository;
import com.KEYSTONE.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final SiteRepository siteRepository;

    public ReportingService(WorkOrderRepository workOrderRepository,
                            UserRepository userRepository,
                            SiteRepository siteRepository) {
        this.workOrderRepository = workOrderRepository;
        this.userRepository = userRepository;
        this.siteRepository = siteRepository;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        List<WorkOrder> allOrders = workOrderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        DashboardSummaryResponse res = new DashboardSummaryResponse();
        res.setTotalWorkOrders(allOrders.size());

        // Status counts
        Map<String, Long> statusCounts = new HashMap<>();
        for (WorkOrderStatus status : WorkOrderStatus.values()) {
            statusCounts.put(status.name(), 0L);
        }
        for (WorkOrder order : allOrders) {
            String statusKey = order.getStatus() != null ? order.getStatus().name() : "NEW";
            statusCounts.put(statusKey, statusCounts.getOrDefault(statusKey, 0L) + 1);
        }
        res.setStatusCounts(statusCounts);

        // Active & Overdue work orders
        long activeCount = allOrders.stream()
                .filter(w -> w.getStatus() != WorkOrderStatus.COMPLETED
                        && w.getStatus() != WorkOrderStatus.CLOSED
                        && w.getStatus() != WorkOrderStatus.CANCELLED)
                .count();
        res.setActiveWorkOrders(activeCount);

        long overdueCount = allOrders.stream()
                .filter(w -> w.getStatus() != WorkOrderStatus.COMPLETED
                        && w.getStatus() != WorkOrderStatus.CLOSED
                        && w.getStatus() != WorkOrderStatus.CANCELLED
                        && w.getSlaDueAt() != null
                        && now.isAfter(w.getSlaDueAt()))
                .count();
        res.setOverdueWorkOrders(overdueCount);

        // SLA Compliance Rate
        List<WorkOrder> finishedOrders = allOrders.stream()
                .filter(w -> w.getStatus() == WorkOrderStatus.COMPLETED || w.getStatus() == WorkOrderStatus.CLOSED)
                .toList();

        if (finishedOrders.isEmpty()) {
            res.setSlaComplianceRate(100.0);
        } else {
            long slaMetCount = finishedOrders.stream()
                    .filter(w -> w.getSlaDueAt() != null) // assume met if finished
                    .count();
            double rate = Math.round(((double) slaMetCount / finishedOrders.size()) * 1000.0) / 10.0;
            res.setSlaComplianceRate(rate);
        }

        // Technician Breakdown
        List<User> technicians = userRepository.findByRole(Role.TECHNICIAN);
        List<TechnicianPerformanceDto> techBreakdown = technicians.stream().map(tech -> {
            long techActive = allOrders.stream()
                    .filter(w -> w.getAssignee() != null && w.getAssignee().getId().equals(tech.getId())
                            && w.getStatus() != WorkOrderStatus.COMPLETED
                            && w.getStatus() != WorkOrderStatus.CLOSED
                            && w.getStatus() != WorkOrderStatus.CANCELLED)
                    .count();

            long techCompleted = allOrders.stream()
                    .filter(w -> w.getAssignee() != null && w.getAssignee().getId().equals(tech.getId())
                            && (w.getStatus() == WorkOrderStatus.COMPLETED || w.getStatus() == WorkOrderStatus.CLOSED))
                    .count();

            return new TechnicianPerformanceDto(tech.getId(), tech.getEmail(), techActive, techCompleted);
        }).collect(Collectors.toList());
        res.setTechnicianBreakdown(techBreakdown);

        // Site Breakdown
        List<SiteWorkloadDto> siteBreakdown = siteRepository.findAll().stream().map(site -> {
            long siteActive = allOrders.stream()
                    .filter(w -> w.getSite() != null && w.getSite().getId().equals(site.getId())
                            && w.getStatus() != WorkOrderStatus.COMPLETED
                            && w.getStatus() != WorkOrderStatus.CLOSED
                            && w.getStatus() != WorkOrderStatus.CANCELLED)
                    .count();

            String custName = site.getCustomer() != null ? site.getCustomer().getName() : "Unknown";
            return new SiteWorkloadDto(site.getId(), site.getName(), custName, siteActive);
        }).filter(s -> s.getActiveJobs() > 0 || allOrders.isEmpty()).collect(Collectors.toList());
        res.setSiteBreakdown(siteBreakdown);

        return res;
    }
}
