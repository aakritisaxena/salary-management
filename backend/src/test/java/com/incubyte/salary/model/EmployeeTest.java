package com.incubyte.salary.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Employee validEmployee() {
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setFullName("Jane Doe");
        e.setJobTitle("Software Engineer");
        e.setCountry("IN");
        e.setSalary(new BigDecimal("50000"));
        e.setCurrency("INR");
        e.setEmail("jane.doe@example.com");
        e.setDepartment("Engineering");
        e.setHireDate(LocalDate.now().minusDays(1));
        return e;
    }

    @Test
    void validEmployee_hasNoViolations() {
        assertThat(validator.validate(validEmployee())).isEmpty();
    }

    @Test
    void rejectsBlankFullName() {
        Employee e = validEmployee();
        e.setFullName("   ");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullName"));
    }

    @Test
    void rejectsNullFullName() {
        Employee e = validEmployee();
        e.setFullName(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullName"));
    }

    @Test
    void rejectsFullNameExceedingMaxLength() {
        Employee e = validEmployee();
        e.setFullName("A".repeat(201));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("fullName"));
    }

    @Test
    void rejectsNullJobTitle() {
        Employee e = validEmployee();
        e.setJobTitle(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("jobTitle"));
    }

    @Test
    void rejectsBlankJobTitle() {
        Employee e = validEmployee();
        e.setJobTitle("   ");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("jobTitle"));
    }

    @Test
    void rejectsJobTitleExceedingMaxLength() {
        Employee e = validEmployee();
        e.setJobTitle("A".repeat(101));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("jobTitle"));
    }

    @Test
    void rejectsNullSalary() {
        Employee e = validEmployee();
        e.setSalary(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("salary"));
    }

    @Test
    void rejectsNegativeSalary() {
        Employee e = validEmployee();
        e.setSalary(new BigDecimal("-0.01"));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("salary"));
    }

    @Test
    void acceptsZeroSalary() {
        Employee e = validEmployee();
        e.setSalary(java.math.BigDecimal.ZERO);
        assertThat(validator.validate(e)).isEmpty();
    }

    @Test
    void rejectsFutureHireDate() {
        Employee e = validEmployee();
        e.setHireDate(LocalDate.now().plusDays(1));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("hireDate"));
    }

    @Test
    void acceptsTodayAsHireDate() {
        Employee e = validEmployee();
        e.setHireDate(LocalDate.now());
        assertThat(validator.validate(e)).isEmpty();
    }

    @Test
    void rejectsNullEmail() {
        Employee e = validEmployee();
        e.setEmail(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void rejectsInvalidEmail() {
        Employee e = validEmployee();
        e.setEmail("not-an-email");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void rejectsNullCurrency() {
        Employee e = validEmployee();
        e.setCurrency(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("currency"));
    }

    @Test
    void rejectsCurrencyNotThreeChars() {
        Employee e = validEmployee();
        e.setCurrency("IN");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("currency"));
    }

    @Test
    void rejectsCurrencyExceedingThreeChars() {
        Employee e = validEmployee();
        e.setCurrency("INRR");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("currency"));
    }

    @Test
    void rejectsNullDepartment() {
        Employee e = validEmployee();
        e.setDepartment(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("department"));
    }

    @Test
    void rejectsBlankDepartment() {
        Employee e = validEmployee();
        e.setDepartment("   ");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("department"));
    }

    @Test
    void rejectsDepartmentExceedingMaxLength() {
        Employee e = validEmployee();
        e.setDepartment("A".repeat(101));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("department"));
    }

    @Test
    void rejectsNullCountry() {
        Employee e = validEmployee();
        e.setCountry(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
    }

    @Test
    void rejectsBlankCountry() {
        Employee e = validEmployee();
        e.setCountry("   ");
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
    }

    @Test
    void rejectsCountryExceedingMaxLength() {
        Employee e = validEmployee();
        e.setCountry("A".repeat(101));
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("country"));
    }

    @Test
    void rejectsNullHireDate() {
        Employee e = validEmployee();
        e.setHireDate(null);
        assertThat(validator.validate(e))
                .anyMatch(v -> v.getPropertyPath().toString().equals("hireDate"));
    }
}
