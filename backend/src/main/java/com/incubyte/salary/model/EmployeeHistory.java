package com.incubyte.salary.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_history")
public class EmployeeHistory {

    @Id
    @Column(columnDefinition = "TEXT")
    private UUID id;

    @NotNull
    @Column(nullable = false, columnDefinition = "TEXT")
    private UUID employeeId;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String fullName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String jobTitle;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String country;

    @NotNull
    @DecimalMin("0")
    @Column(nullable = false)
    private BigDecimal salary;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    private String currency;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String department;

    @NotNull
    @PastOrPresent
    @Column(nullable = false)
    private LocalDate hireDate;

    @NotNull
    @Column(nullable = false)
    private Instant relievedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public Instant getRelievedAt() { return relievedAt; }
    public void setRelievedAt(Instant relievedAt) { this.relievedAt = relievedAt; }

    public Instant getCreatedAt() { return createdAt; }
}
