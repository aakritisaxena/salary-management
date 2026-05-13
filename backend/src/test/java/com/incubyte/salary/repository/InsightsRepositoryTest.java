package com.incubyte.salary.repository;

import com.incubyte.salary.dto.CountryInsight;
import com.incubyte.salary.dto.DepartmentInsight;
import com.incubyte.salary.dto.JobTitleInsight;
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

    @Test
    void findSalaryStatsByJobTitle_withoutFilter_returnsAllJobTitles() {
        employeeRepository.deleteAll();
        employeeRepository.save(employeeWithTitle("t1@x.com", "Manager", "IN", new BigDecimal("100000")));
        employeeRepository.save(employeeWithTitle("t2@x.com", "Manager", "US", new BigDecimal("120000")));
        employeeRepository.save(employeeWithTitle("t3@x.com", "Analyst", "IN", new BigDecimal("60000")));

        List<JobTitleInsight> stats = employeeRepository.findSalaryStatsByJobTitle(null);

        assertThat(stats).hasSize(2);

        JobTitleInsight manager = stats.stream()
                .filter(s -> s.jobTitle().equals("Manager")).findFirst().orElseThrow();
        assertThat(manager.headcount()).isEqualTo(2);
        assertThat(manager.averageSalary()).isCloseTo(110000.0, offset(0.01));
        assertThat(manager.minSalary()).isEqualByComparingTo("100000");
        assertThat(manager.maxSalary()).isEqualByComparingTo("120000");
    }

    @Test
    void findSalaryStatsByJobTitle_withCountryFilter_returnsOnlyMatchingCountry() {
        employeeRepository.deleteAll();
        employeeRepository.save(employeeWithTitle("t4@x.com", "Manager", "IN", new BigDecimal("100000")));
        employeeRepository.save(employeeWithTitle("t5@x.com", "Manager", "US", new BigDecimal("120000")));
        employeeRepository.save(employeeWithTitle("t6@x.com", "Analyst", "IN", new BigDecimal("60000")));

        List<JobTitleInsight> stats = employeeRepository.findSalaryStatsByJobTitle("IN");

        assertThat(stats).hasSize(2);

        JobTitleInsight manager = stats.stream()
                .filter(s -> s.jobTitle().equals("Manager")).findFirst().orElseThrow();
        assertThat(manager.headcount()).isEqualTo(1);
        assertThat(manager.averageSalary()).isCloseTo(100000.0, offset(0.01));

        JobTitleInsight analyst = stats.stream()
                .filter(s -> s.jobTitle().equals("Analyst")).findFirst().orElseThrow();
        assertThat(analyst.headcount()).isEqualTo(1);
    }

    @Test
    void findSalaryStatsByJobTitle_orderedByAverageSalaryDescending() {
        employeeRepository.deleteAll();
        employeeRepository.save(employeeWithTitle("t7@x.com", "Junior",  "IN", new BigDecimal("40000")));
        employeeRepository.save(employeeWithTitle("t8@x.com", "Senior",  "IN", new BigDecimal("90000")));
        employeeRepository.save(employeeWithTitle("t9@x.com", "Manager", "IN", new BigDecimal("120000")));

        List<JobTitleInsight> stats = employeeRepository.findSalaryStatsByJobTitle(null);

        assertThat(stats).extracting(JobTitleInsight::jobTitle)
                .containsExactly("Manager", "Senior", "Junior");
    }

    private Employee employeeWithTitle(String email, String jobTitle, String country, BigDecimal salary) {
        Employee e = new Employee();
        e.setId(UUID.randomUUID());
        e.setFullName("Test Employee");
        e.setJobTitle(jobTitle);
        e.setCountry(country);
        e.setSalary(salary);
        e.setCurrency("USD");
        e.setEmail(email);
        e.setDepartment("Engineering");
        e.setHireDate(LocalDate.now().minusDays(1));
        return e;
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
