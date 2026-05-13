package com.incubyte.salary.repository;

import com.incubyte.salary.model.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, UUID> {

    long countByCountry(String country);
}
