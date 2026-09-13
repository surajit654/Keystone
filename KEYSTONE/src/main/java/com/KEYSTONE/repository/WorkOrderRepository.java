package com.KEYSTONE.repository;

import com.KEYSTONE.model.WorkOrder;
import com.KEYSTONE.model.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long>, JpaSpecificationExecutor<WorkOrder> {

    Optional<WorkOrder> findByCode(String code);

    List<WorkOrder> findByCustomerId(Long customerId);

    List<WorkOrder> findByAssigneeId(Long assigneeId);

    List<WorkOrder> findByStatus(WorkOrderStatus status);

    @Query("SELECT COUNT(w) FROM WorkOrder w WHERE w.code LIKE :prefix%")
    long countByCodePrefix(@Param("prefix") String prefix);

    Page<WorkOrder> findByCustomerId(Long customerId, Pageable pageable);
}
