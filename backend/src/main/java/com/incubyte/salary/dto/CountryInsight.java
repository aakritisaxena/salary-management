package com.incubyte.salary.dto;

import java.math.BigDecimal;

public record CountryInsight(
        String country,
        long headcount,
        Double averageSalary,
        BigDecimal minSalary,
        BigDecimal maxSalary
) {}
