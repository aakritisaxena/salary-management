package com.incubyte.salary.controller;

import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.model.Employee;
import com.incubyte.salary.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String department,
            @PageableDefault(size = 50) Pageable pageable) {
        return employeeService.findAll(country, department, pageable)
                .map(EmployeeResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@RequestBody @Valid EmployeeRequest request) {
        return EmployeeResponse.from(employeeService.create(toEntity(request)));
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable UUID id) {
        return EmployeeResponse.from(employeeService.findById(id));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable UUID id,
                                   @RequestBody @Valid EmployeeRequest request) {
        return EmployeeResponse.from(employeeService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable UUID id) {
        employeeService.delete(id);
        return Map.of("message", "Employee deleted successfully");
    }

    private Employee toEntity(EmployeeRequest r) {
        Employee e = new Employee();
        e.setFullName(r.fullName());
        e.setJobTitle(r.jobTitle());
        e.setCountry(r.country());
        e.setSalary(r.salary());
        e.setCurrency(r.currency());
        e.setEmail(r.email());
        e.setDepartment(r.department());
        e.setHireDate(r.hireDate());
        return e;
    }
}
