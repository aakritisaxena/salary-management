package com.incubyte.salary.service;

import com.incubyte.salary.exception.EmployeeNotFoundException;
import com.incubyte.salary.model.Employee;
import com.incubyte.salary.model.EmployeeHistory;
import com.incubyte.salary.repository.EmployeeHistoryRepository;
import com.incubyte.salary.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeHistoryRepository employeeHistoryRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(UUID.randomUUID());
        employee.setFullName("Jane Doe");
        employee.setJobTitle("Engineer");
        employee.setCountry("IN");
        employee.setSalary(new BigDecimal("50000"));
        employee.setCurrency("INR");
        employee.setEmail("jane@example.com");
        employee.setDepartment("Engineering");
        employee.setHireDate(LocalDate.now().minusDays(1));
    }

    @Test
    void create_savesAndReturnsEmployee() {
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.create(employee);

        assertThat(result).isEqualTo(employee);
        verify(employeeRepository).save(employee);
    }

    @Test
    void findById_returnsEmployeeWhenPresent() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        Employee result = employeeService.findById(employee.getId());

        assertThat(result).isEqualTo(employee);
    }

    @Test
    void findById_throwsNotFoundWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(id))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void findAll_returnsPaginatedResults() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findByFilters(null, null, pageable)).thenReturn(page);

        Page<Employee> result = employeeService.findAll(null, null, pageable);

        assertThat(result.getContent()).containsExactly(employee);
    }

    @Test
    void findAll_passesFiltersToRepository() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findByFilters("IN", "Engineering", pageable)).thenReturn(page);

        Page<Employee> result = employeeService.findAll("IN", "Engineering", pageable);

        assertThat(result.getContent()).containsExactly(employee);
        verify(employeeRepository).findByFilters("IN", "Engineering", pageable);
    }

    @Test
    void update_updatesFieldsAndSaves() {
        Employee updates = new Employee();
        updates.setFullName("Jane Smith");
        updates.setJobTitle("Senior Engineer");
        updates.setCountry("US");
        updates.setSalary(new BigDecimal("80000"));
        updates.setCurrency("USD");
        updates.setEmail("janesmith@example.com");
        updates.setDepartment("Platform");
        updates.setHireDate(LocalDate.now().minusDays(10));

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.update(employee.getId(), updates);

        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        Employee saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("Jane Smith");
        assertThat(saved.getSalary()).isEqualByComparingTo("80000");
        assertThat(saved.getCurrency()).isEqualTo("USD");
    }

    @Test
    void update_throwsNotFoundWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(id, employee))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void delete_copiesRecordToHistoryThenDeletes() {
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        employeeService.delete(employee.getId());

        ArgumentCaptor<EmployeeHistory> historyCaptor = ArgumentCaptor.forClass(EmployeeHistory.class);
        verify(employeeHistoryRepository).save(historyCaptor.capture());
        EmployeeHistory saved = historyCaptor.getValue();
        assertThat(saved.getEmployeeId()).isEqualTo(employee.getId());
        assertThat(saved.getEmail()).isEqualTo(employee.getEmail());
        assertThat(saved.getRelievedAt()).isNotNull();

        verify(employeeRepository).delete(employee);
    }

    @Test
    void delete_throwsNotFoundWhenAbsent() {
        UUID id = UUID.randomUUID();
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(id))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(employeeHistoryRepository, never()).save(any());
        verify(employeeRepository, never()).delete(any());
    }
}
