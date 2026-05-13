package com.incubyte.salary.controller;

import com.incubyte.salary.dto.InsightsResponse;
import com.incubyte.salary.dto.JobTitleInsight;
import com.incubyte.salary.service.SalaryInsightsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
public class InsightsController {

    private final SalaryInsightsService salaryInsightsService;

    public InsightsController(SalaryInsightsService salaryInsightsService) {
        this.salaryInsightsService = salaryInsightsService;
    }

    @GetMapping
    public InsightsResponse getInsights() {
        return salaryInsightsService.getInsights();
    }

    @GetMapping("/job-titles")
    public List<JobTitleInsight> getJobTitleInsights(@RequestParam(required = false) String country) {
        return salaryInsightsService.getJobTitleInsights(country);
    }
}
