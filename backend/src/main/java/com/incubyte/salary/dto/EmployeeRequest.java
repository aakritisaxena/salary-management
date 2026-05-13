package com.incubyte.salary.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank @Size(max = 200) String fullName,
        @NotBlank @Size(max = 100) String jobTitle,
        @NotBlank @Size(max = 100) String country,
        @NotNull @DecimalMin("0") BigDecimal salary,
        @NotNull @Size(min = 3, max = 3) String currency,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 100) String department,
        @NotNull @PastOrPresent LocalDate hireDate
) {}
