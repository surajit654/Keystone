package com.KEYSTONE.service;

import com.KEYSTONE.model.WorkOrder;
import com.KEYSTONE.model.WorkOrderStatus;
import com.KEYSTONE.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SlaMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitoringService.class);

    private final WorkOrderRepository workOrderRepository;

    public SlaMonitoringService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    /**
     * Runs periodically every 60 seconds to scan active work orders and detect SLA breaches.
     */
    @Scheduled(fixedRate = 60000)
    public void scanAndCheckSlaBreaches() {
        LocalDateTime now = LocalDateTime.now();
        List<WorkOrder> activeOrders = workOrderRepository.findAll().stream()
                .filter(w -> w.getStatus() != WorkOrderStatus.COMPLETED
                        && w.getStatus() != WorkOrderStatus.CLOSED
                        && w.getStatus() != WorkOrderStatus.CANCELLED)
                .toList();

        int breachCount = 0;
        int atRiskCount = 0;

        for (WorkOrder order : activeOrders) {
            if (order.getSlaDueAt() == null) continue;

            if (now.isAfter(order.getSlaDueAt())) {
                breachCount++;
                log.warn("🚨 [SLA BREACH ALERT] WorkOrder {} ('{}') has BREACHED its SLA due date: {}",
                        order.getCode(), order.getTitle(), order.getSlaDueAt());
            } else if (now.isAfter(order.getSlaDueAt().minusHours(1))) {
                atRiskCount++;
                log.info("⏳ [SLA AT RISK] WorkOrder {} ('{}') is nearing SLA deadline: {}",
                        order.getCode(), order.getTitle(), order.getSlaDueAt());
            }
        }

        if (breachCount > 0 || atRiskCount > 0) {
            log.info("📊 [SLA Monitor Summary] Total Active: {}, At Risk (<1h): {}, Breached: {}",
                    activeOrders.size(), atRiskCount, breachCount);
        }
    }
}
