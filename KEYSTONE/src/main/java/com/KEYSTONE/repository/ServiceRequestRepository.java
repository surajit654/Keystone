package com.KEYSTONE.repository;

import com.KEYSTONE.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<ServiceRequest> findByAssignedTechnicianIdIsNullOrderByCreatedAtDesc();

    List<ServiceRequest> findByAssignedTechnicianIdOrderByCreatedAtDesc(Long assignedTechnicianId);

    List<ServiceRequest> findAllByOrderByCreatedAtDesc();
}