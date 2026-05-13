package com.incubyte.salary.repository;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@DataJpaTest
class InsightsRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        employeeRepository.save(employee("a@x.com", "Engineering", "IN", new BigDecimal("60000")));
        employeeRepository.save(employee("b@x.com", "Engineering", "IN", new BigDecimal("90000")));
        employeeRepository.save(employee("c@x.com", "Sales",       "US", new BigDecimal("50000")));
        employeeRepository.save(employee("d@x.com", "Sales",       "US", new BigDecimal("70000")));
        employeeRepository.save(employee("e@x.com", "HR",          "IN", new BigDecimal("40000")));
    }

    @Test
    void findSalaryStatsByDepartment_groupsAndAggregatesCorrectly() {
        List<DepartmentInsight> stats = employeeRepository.findSalaryStatsByDepartment();

        assertThat(stats).hasSize(3);

        DepartmentInsight engineering = stats.stream()
                .filter(s -> s.department().equals("Engineering")).findFirst().orElseThrow();
        assertThat(engineering.headcount()).isEqualTo(2);
        assertThat(engineering.averageSalary()).isCloseTo(75000.0, offset(0.01));
        assertThat(engineering.minSalary()).isEqualByComparingTo("60000");
        assertThat(engineering.maxSalary()).isEqualByComparingTo("90000");
    }

    @Test
    void findSalaryStatsByCountry_groupsAndAggregatesCorrectly() {
        List<CountryInsight> stats = employeeRepository.findSalaryStatsByCountry();

        assertThat(stats).hasSize(2);

        CountryInsight india = stats.stream()
                .filter(s -> s.country().equals("IN")).findFirst().orElseThrow();
        assertThat(india.headcount()).isEqualTo(3);
        assertThat(india.averageSalary()).isCloseTo(63333.33, offset(0.01));
        assertThat(india.minSalary()).isEqualByComparingTo("40000");
        assertThat(india.maxSalary()).isEqualByComparingTo("90000");
    }

    @Test
    void findSalaryStatsByDepartment_orderedAlphabetically() {
        List<DepartmentInsight> stats = employeeRepository.findSalaryStatsByDepartment();
        List<String> names = stats.stream().map(DepartmentInsight::department).toList();
        assertThat(names).isSorted();
    }

    @Test
    void findSalaryStatsByCountry_orderedAlphabetically() {
        List<CountryInsight> stats = employeeRepository.findSalaryStatsByCountry();
        List<String> names = stats.stream().map(CountryInsight::country).toList();
        assertThat(names).isSorted();
    }

    private Employee employee(String email, String department, String country, BigDecimal salary) {
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setFullName("Test Employee");
        e.setJobTitle("Engineer");
        e.setCountry(country);
        e.setSalary(salary);
        e.setCurrency("USD");
        e.setEmail(email);
        e.setDepartment(department);
        e.setHireDate(LocalDate.now().minusDays(1));
        return e;
    }
}
