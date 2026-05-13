package com.incubyte.salary.dto;

import java.math.BigDecimal;

public record DepartmentInsight(
        String department,
        long headcount,
        Double averageSalary,
        BigDecimal minSalary,
        BigDecimal maxSalary
) {}
