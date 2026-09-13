package com.KEYSTONE.repository;

import com.KEYSTONE.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartRepository extends JpaRepository<Part, Long> {

    Optional<Part> findByName(String name);

    Optional<Part> findByPartNumber(String partNumber);
}
