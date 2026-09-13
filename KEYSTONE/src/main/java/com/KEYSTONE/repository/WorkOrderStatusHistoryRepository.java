package com.KEYSTONE.repository;

import com.KEYSTONE.model.WorkOrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderStatusHistoryRepository extends JpaRepository<WorkOrderStatusHistory, Long> {

    List<WorkOrderStatusHistory> findByWorkOrderIdOrderByChangedAtAsc(Long workOrderId);

    List<WorkOrderStatusHistory> findByWorkOrderIdOrderByChangedAtDesc(Long workOrderId);
}
