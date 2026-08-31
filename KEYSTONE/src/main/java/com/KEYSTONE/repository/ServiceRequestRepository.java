package com.KEYSTONE.repository;

import com.KEYSTONE.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

}