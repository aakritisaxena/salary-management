package com.incubyte.salary.seeder;

import com.incubyte.salary.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestPropertySource(properties = "app.seed.enabled=true")
class DataSeederTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DataSeeder dataSeeder;

    @Test
    void seed_insertsExpectedNumberOfEmployees() {
        long count = employeeRepository.count();
        assertThat(count).isGreaterThanOrEqualTo(DataSeeder.SEED_COUNT);
    }

    @Test
    void seed_isIdempotent() {
        long beforeCount = employeeRepository.count();
        dataSeeder.seed();
        long afterCount = employeeRepository.count();
        assertThat(afterCount).isEqualTo(beforeCount);
    }

    @Test
    void seed_employeesHaveValidData() {
        employeeRepository.findAll().forEach(employee -> {
            assertThat(employee.getFullName()).isNotBlank();
            assertThat(employee.getEmail()).contains("@");
            assertThat(employee.getSalary()).isPositive();
            assertThat(employee.getCurrency()).hasSize(3);
            assertThat(employee.getHireDate()).isNotNull();
            assertThat(employee.getCountry()).isNotBlank();
            assertThat(employee.getDepartment()).isNotBlank();
        });
    }

    @Test
    void loadLines_throwsIllegalStateExceptionWhenResourceNotFound() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(dataSeeder, "loadLines", "data/nonexistent.txt"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to load resource: data/nonexistent.txt");
    }
}
