package com.incubyte.salary.dto;

import java.math.BigDecimal;

public record JobTitleInsight(
        String jobTitle,
        long headcount,
        Double averageSalary,
        BigDecimal minSalary,
        BigDecimal maxSalary
) {}
