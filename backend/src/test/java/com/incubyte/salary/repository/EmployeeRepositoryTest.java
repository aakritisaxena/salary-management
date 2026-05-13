package com.incubyte.salary.repository;

import com.incubyte.salary.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee buildEmployee(String fullName, String email, String country, String department) {
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setFullName(fullName);
        e.setJobTitle("Engineer");
        e.setCountry(country);
        e.setSalary(new BigDecimal("50000"));
        e.setCurrency("INR");
        e.setEmail(email);
        e.setDepartment(department);
        e.setHireDate(LocalDate.now().minusDays(1));
        return e;
    }

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void saveAndFindById() {
        Employee saved = employeeRepository.save(buildEmployee("Jane Doe", "jane@example.com", "IN", "Engineering"));
        Optional<Employee> found = employeeRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Jane Doe");
    }

    @Test
    void findByFilters_noFilter_returnsAll() {
        employeeRepository.save(buildEmployee("Alice", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Bob", "bob@example.com", "US", "Sales"));

        Page<Employee> result = employeeRepository.findByFilters(null, null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_withCountry_returnsOnlyMatchingCountry() {
        employeeRepository.save(buildEmployee("Alice", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Bob", "bob@example.com", "US", "Engineering"));

        Page<Employee> result = employeeRepository.findByFilters("IN", null, null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Alice");
    }

    @Test
    void findByFilters_withDepartment_returnsOnlyMatchingDepartment() {
        employeeRepository.save(buildEmployee("Alice", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Bob", "bob@example.com", "IN", "Sales"));

        Page<Employee> result = employeeRepository.findByFilters(null, "Sales", null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Bob");
    }

    @Test
    void findByFilters_withCountryAndDepartment_returnsOnlyExactMatch() {
        employeeRepository.save(buildEmployee("Alice", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Bob", "bob@example.com", "IN", "Sales"));
        employeeRepository.save(buildEmployee("Charlie", "charlie@example.com", "US", "Engineering"));

        Page<Employee> result = employeeRepository.findByFilters("IN", "Engineering", null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Alice");
    }

    @Test
    void findByFilters_withName_returnsPartialCaseInsensitiveMatch() {
        employeeRepository.save(buildEmployee("Alice Smith", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Bob Johnson", "bob@example.com", "US", "Sales"));

        Page<Employee> result = employeeRepository.findByFilters(null, null, "alice", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("Alice Smith");
    }

    @Test
    void findByFilters_withName_matchesPartialName() {
        employeeRepository.save(buildEmployee("Alice Smith", "alice@example.com", "IN", "Engineering"));
        employeeRepository.save(buildEmployee("Alice Brown", "abrown@example.com", "US", "Sales"));
        employeeRepository.save(buildEmployee("Bob Johnson", "bob@example.com", "US", "Sales"));

        Page<Employee> result = employeeRepository.findByFilters(null, null, "alice", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findByFilters_respectsPageSize() {
        for (int i = 0; i < 5; i++) {
            employeeRepository.save(buildEmployee("Employee " + i, "emp" + i + "@example.com", "IN", "Engineering"));
        }

        Page<Employee> result = employeeRepository.findByFilters(null, null, null, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(5);
    }

    @Test
    void duplicateEmailThrowsException() {
        employeeRepository.save(buildEmployee("Alice", "same@example.com", "IN", "Engineering"));

        assertThatThrownBy(() -> {
            employeeRepository.save(buildEmployee("Bob", "same@example.com", "US", "Sales"));
            employeeRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
