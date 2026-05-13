package com.incubyte.salary.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeHistoryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private EmployeeHistory validHistory() {
        EmployeeHistory h = new EmployeeHistory();
        h.setEmployeeId(UUID.randomUUID());
        h.setFullName("Jane Doe");
        h.setJobTitle("Software Engineer");
        h.setCountry("IN");
        h.setSalary(new BigDecimal("50000"));
        h.setCurrency("INR");
        h.setEmail("jane.doe@example.com");
        h.setDepartment("Engineering");
        h.setHireDate(LocalDate.now().minusDays(1));
        h.setRelievedAt(Instant.now());
        return h;
    }

    @Test
    void validHistory_hasNoViolations() {
        assertThat(validator.validate(validHistory())).isEmpty();
    }

    @Test
    void rejectsNullEmployeeId() {
        EmployeeHistory h = validHistory();
        h.setEmployeeId(null);
        assertThat(validator.validate(h))
                .anyMatch(v -> v.getPropertyPath().toString().equals("employeeId"));
    }

    @Test
    void rejectsNullRelievedAt() {
        EmployeeHistory h = validHistory();
        h.setRelievedAt(null);
        assertThat(validator.validate(h))
                .anyMatch(v -> v.getPropertyPath().toString().equals("relievedAt"));
    }
}
