package com.incubyte.salary.controller;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.InsightsResponse;
import com.incubyte.salary.dto.JobTitleInsight;
import com.incubyte.salary.service.SalaryInsightsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsightsController.class)
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SalaryInsightsService salaryInsightsService;

    @Test
    void getInsights_returns200WithFullResponse() throws Exception {
        when(salaryInsightsService.getInsights()).thenReturn(new InsightsResponse(
                100L, 80000.0, new BigDecimal("40000"), new BigDecimal("150000"),
                List.of(new DepartmentInsight("Engineering", 60, 90000.0, new BigDecimal("50000"), new BigDecimal("150000"))),
                List.of(new CountryInsight("IN", 50, 80000.0, new BigDecimal("40000"), new BigDecimal("150000")))
        ));

        mockMvc.perform(get("/api/insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(100))
                .andExpect(jsonPath("$.averageSalary").value(80000.0))
                .andExpect(jsonPath("$.byDepartment[0].department").value("Engineering"))
                .andExpect(jsonPath("$.byDepartment[0].headcount").value(60))
                .andExpect(jsonPath("$.byCountry[0].country").value("IN"));
    }

    @Test
    void getInsights_returnsEmptyListsWhenNoData() throws Exception {
        when(salaryInsightsService.getInsights()).thenReturn(
                new InsightsResponse(0L, 0.0, BigDecimal.ZERO, BigDecimal.ZERO, List.of(), List.of())
        );

        mockMvc.perform(get("/api/insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(0))
                .andExpect(jsonPath("$.byDepartment").isEmpty())
                .andExpect(jsonPath("$.byCountry").isEmpty());
    }

    @Test
    void getJobTitleInsights_withCountryParam_returns200WithFilteredData() throws Exception {
        when(salaryInsightsService.getJobTitleInsights("IN")).thenReturn(List.of(
                new JobTitleInsight("Engineer", 5, 80000.0, new BigDecimal("50000"), new BigDecimal("120000")),
                new JobTitleInsight("Analyst",  2, 60000.0, new BigDecimal("45000"), new BigDecimal("75000"))
        ));

        mockMvc.perform(get("/api/insights/job-titles").param("country", "IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].jobTitle").value("Engineer"))
                .andExpect(jsonPath("$[0].headcount").value(5))
                .andExpect(jsonPath("$[0].averageSalary").value(80000.0))
                .andExpect(jsonPath("$[0].minSalary").value(50000))
                .andExpect(jsonPath("$[0].maxSalary").value(120000))
                .andExpect(jsonPath("$[1].jobTitle").value("Analyst"));
    }

    @Test
    void getJobTitleInsights_withoutCountryParam_returns200WithAllData() throws Exception {
        when(salaryInsightsService.getJobTitleInsights(null)).thenReturn(List.of(
                new JobTitleInsight("Manager", 3, 110000.0, new BigDecimal("90000"), new BigDecimal("130000"))
        ));

        mockMvc.perform(get("/api/insights/job-titles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].jobTitle").value("Manager"))
                .andExpect(jsonPath("$[0].headcount").value(3));
    }

    @Test
    void getJobTitleInsights_returnsEmptyArrayWhenNoData() throws Exception {
        when(salaryInsightsService.getJobTitleInsights(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/insights/job-titles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
