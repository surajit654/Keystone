package com.KEYSTONE.repository;

import com.KEYSTONE.model.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {

    List<TimeLog> findByWorkOrderId(Long workOrderId);
}
