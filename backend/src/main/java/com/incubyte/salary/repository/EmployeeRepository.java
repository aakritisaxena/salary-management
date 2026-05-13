package com.incubyte.salary.repository;

import com.incubyte.salary.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @Query("SELECT e FROM Employee e WHERE (:country IS NULL OR e.country = :country) AND (:department IS NULL OR e.department = :department)")
    Page<Employee> findByFilters(@Param("country") String country,
                                 @Param("department") String department,
                                 Pageable pageable);
}
