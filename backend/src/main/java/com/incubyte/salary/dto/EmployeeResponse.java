package com.incubyte.salary.dto;

import com.incubyte.salary.model.Employee;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID id,
        String fullName,
        String jobTitle,
        String country,
        BigDecimal salary,
        String currency,
        String email,
        String department,
        LocalDate hireDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmployeeResponse from(Employee e) {
        return new EmployeeResponse(
                e.getId(), e.getFullName(), e.getJobTitle(), e.getCountry(),
                e.getSalary(), e.getCurrency(), e.getEmail(), e.getDepartment(),
                e.getHireDate(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
