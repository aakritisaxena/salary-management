package com.incubyte.salary.service;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.InsightsResponse;
import com.incubyte.salary.dto.JobTitleInsight;
import com.incubyte.salary.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SalaryInsightsServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SalaryInsightsService salaryInsightsService;

    @Test
    void getInsights_returnsAggregatedResponse() {
        when(employeeRepository.count()).thenReturn(100L);
        when(employeeRepository.findSalaryStatsByDepartment()).thenReturn(List.of(
                new DepartmentInsight("Engineering", 60, 90000.0, new BigDecimal("50000"), new BigDecimal("150000")),
                new DepartmentInsight("Sales", 40, 70000.0, new BigDecimal("40000"), new BigDecimal("120000"))
        ));
        when(employeeRepository.findSalaryStatsByCountry()).thenReturn(List.of(
                new CountryInsight("IN", 50, 80000.0, new BigDecimal("40000"), new BigDecimal("150000")),
                new CountryInsight("US", 50, 85000.0, new BigDecimal("50000"), new BigDecimal("150000"))
        ));

        InsightsResponse response = salaryInsightsService.getInsights();

        assertThat(response.totalEmployees()).isEqualTo(100);
        assertThat(response.byDepartment()).hasSize(2);
        assertThat(response.byCountry()).hasSize(2);
        assertThat(response.byDepartment().get(0).department()).isEqualTo("Engineering");
        assertThat(response.byCountry().get(0).country()).isEqualTo("IN");
    }

    @Test
    void getInsights_computesOverallAverageFromDepartmentStats() {
        when(employeeRepository.count()).thenReturn(2L);
        when(employeeRepository.findSalaryStatsByDepartment()).thenReturn(List.of(
                new DepartmentInsight("Engineering", 1, 90000.0, new BigDecimal("90000"), new BigDecimal("90000")),
                new DepartmentInsight("Sales", 1, 60000.0, new BigDecimal("60000"), new BigDecimal("60000"))
        ));
        when(employeeRepository.findSalaryStatsByCountry()).thenReturn(List.of());

        InsightsResponse response = salaryInsightsService.getInsights();

        assertThat(response.minSalary()).isEqualByComparingTo("60000");
        assertThat(response.maxSalary()).isEqualByComparingTo("90000");
    }

    @Test
    void getInsights_handlesEmptyEmployeeTable() {
        when(employeeRepository.count()).thenReturn(0L);
        when(employeeRepository.findSalaryStatsByDepartment()).thenReturn(List.of());
        when(employeeRepository.findSalaryStatsByCountry()).thenReturn(List.of());

        InsightsResponse response = salaryInsightsService.getInsights();

        assertThat(response.totalEmployees()).isZero();
        assertThat(response.byDepartment()).isEmpty();
        assertThat(response.byCountry()).isEmpty();
    }

    @Test
    void getJobTitleInsights_withCountry_delegatesToRepository() {
        List<JobTitleInsight> expected = List.of(
                new JobTitleInsight("Engineer", 10, 80000.0, new BigDecimal("50000"), new BigDecimal("120000"))
        );
        when(employeeRepository.findSalaryStatsByJobTitle("IN")).thenReturn(expected);

        List<JobTitleInsight> result = salaryInsightsService.getJobTitleInsights("IN");

        assertThat(result).isEqualTo(expected);
        verify(employeeRepository).findSalaryStatsByJobTitle("IN");
    }

    @Test
    void getJobTitleInsights_withoutCountry_passesNullToRepository() {
        List<JobTitleInsight> expected = List.of(
                new JobTitleInsight("Engineer", 5, 75000.0, new BigDecimal("50000"), new BigDecimal("100000")),
                new JobTitleInsight("Analyst",  3, 60000.0, new BigDecimal("45000"), new BigDecimal("75000"))
        );
        when(employeeRepository.findSalaryStatsByJobTitle(null)).thenReturn(expected);

        List<JobTitleInsight> result = salaryInsightsService.getJobTitleInsights(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).jobTitle()).isEqualTo("Engineer");
        verify(employeeRepository).findSalaryStatsByJobTitle(null);
    }
}
