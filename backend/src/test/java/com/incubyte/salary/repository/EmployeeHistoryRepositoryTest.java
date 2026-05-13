package com.incubyte.salary.repository;

import com.incubyte.salary.model.EmployeeHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeHistoryRepositoryTest {

    @Autowired
    private EmployeeHistoryRepository employeeHistoryRepository;

    @BeforeEach
    void setUp() {
        employeeHistoryRepository.deleteAll();
    }

    private EmployeeHistory buildHistory(String email, String country) {
        EmployeeHistory h = new EmployeeHistory();
        h.setEmployeeId(UUID.randomUUID());
        h.setFullName("Jane Doe");
        h.setJobTitle("Engineer");
        h.setCountry(country);
        h.setSalary(new BigDecimal("50000"));
        h.setCurrency("INR");
        h.setEmail(email);
        h.setDepartment("Engineering");
        h.setHireDate(LocalDate.now().minusDays(1));
        h.setRelievedAt(Instant.now());
        return h;
    }

    @Test
    void saveAndFindById() {
        EmployeeHistory saved = employeeHistoryRepository.save(buildHistory("jane@example.com", "IN"));
        Optional<EmployeeHistory> found = employeeHistoryRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void countByCountry_returnsCorrectCounts() {
        employeeHistoryRepository.save(buildHistory("a@example.com", "IN"));
        employeeHistoryRepository.save(buildHistory("b@example.com", "IN"));
        employeeHistoryRepository.save(buildHistory("c@example.com", "US"));

        assertThat(employeeHistoryRepository.countByCountry("IN")).isEqualTo(2);
        assertThat(employeeHistoryRepository.countByCountry("US")).isEqualTo(1);
        assertThat(employeeHistoryRepository.countByCountry("GB")).isEqualTo(0);
    }
}
