package com.incubyte.salary.controller;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.InsightsResponse;
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
}
