package com.incubyte.salary.service;

import com.incubyte.salary.exception.EmployeeNotFoundException;
import com.incubyte.salary.model.Employee;
import com.incubyte.salary.model.EmployeeHistory;
import com.incubyte.salary.repository.EmployeeHistoryRepository;
import com.incubyte.salary.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeHistoryRepository employeeHistoryRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           EmployeeHistoryRepository employeeHistoryRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeHistoryRepository = employeeHistoryRepository;
    }

    public Employee create(Employee employee) {
        log.info("Creating employee: {}", employee.getFullName());
        return employeeRepository.save(employee);
    }

    public Employee findById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found: {}", id);
                    return new EmployeeNotFoundException(id);
                });
    }

    public Page<Employee> findAll(String country, String department, String name, Pageable pageable) {
        return employeeRepository.findByFilters(country, department, name, pageable);
    }

    public Employee update(UUID id, Employee updates) {
        Employee existing = findById(id);
        existing.setFullName(updates.getFullName());
        existing.setJobTitle(updates.getJobTitle());
        existing.setCountry(updates.getCountry());
        existing.setSalary(updates.getSalary());
        existing.setCurrency(updates.getCurrency());
        existing.setEmail(updates.getEmail());
        existing.setDepartment(updates.getDepartment());
        existing.setHireDate(updates.getHireDate());
        return employeeRepository.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Archiving employee: {}", id);
        Employee employee = findById(id);
        EmployeeHistory history = toHistory(employee);
        employeeHistoryRepository.save(history);
        employeeRepository.delete(employee);
    }

    private EmployeeHistory toHistory(Employee e) {
        EmployeeHistory h = new EmployeeHistory();
        h.setEmployeeId(e.getId());
        h.setFullName(e.getFullName());
        h.setJobTitle(e.getJobTitle());
        h.setCountry(e.getCountry());
        h.setSalary(e.getSalary());
        h.setCurrency(e.getCurrency());
        h.setEmail(e.getEmail());
        h.setDepartment(e.getDepartment());
        h.setHireDate(e.getHireDate());
        h.setRelievedAt(Instant.now());
        return h;
    }
}
