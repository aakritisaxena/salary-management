package com.incubyte.salary.dto;

import java.math.BigDecimal;
import java.util.List;

public record InsightsResponse(
        long totalEmployees,
        Double averageSalary,
        BigDecimal minSalary,
        BigDecimal maxSalary,
        List<DepartmentInsight> byDepartment,
        List<CountryInsight> byCountry
) {}
