package com.incubyte.salary.service;

import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.InsightsResponse;
import com.incubyte.salary.dto.JobTitleInsight;
import com.incubyte.salary.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SalaryInsightsService {

    private static final Logger log = LoggerFactory.getLogger(SalaryInsightsService.class);

    private final EmployeeRepository employeeRepository;

    public SalaryInsightsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public InsightsResponse getInsights() {
        long total = employeeRepository.count();
        var byDepartment = employeeRepository.findSalaryStatsByDepartment();
        var byCountry = employeeRepository.findSalaryStatsByCountry();

        Double averageSalary = byDepartment.stream()
                .mapToDouble(d -> d.averageSalary() * d.headcount())
                .sum() / (total == 0 ? 1 : total);

        BigDecimal minSalary = byDepartment.stream()
                .map(DepartmentInsight::minSalary)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxSalary = byDepartment.stream()
                .map(DepartmentInsight::maxSalary)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new InsightsResponse(total, total == 0 ? 0.0 : averageSalary, minSalary, maxSalary, byDepartment, byCountry);
    }

    public List<JobTitleInsight> getJobTitleInsights(String country) {
        log.info("Fetching salary insights for country: {}", country);
        return employeeRepository.findSalaryStatsByJobTitle(country);
    }
}
