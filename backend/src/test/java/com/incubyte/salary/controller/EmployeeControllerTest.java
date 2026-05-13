package com.incubyte.salary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.exception.EmployeeNotFoundException;
import com.incubyte.salary.model.Employee;
import com.incubyte.salary.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequest validRequest;

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

        validRequest = new EmployeeRequest(
                "Jane Doe", "Engineer", "IN",
                new BigDecimal("50000"), "INR", "jane@example.com",
                "Engineering", LocalDate.now().minusDays(1)
        );
    }

    @Test
    void getEmployees_returns200WithPagedContent() throws Exception {
        when(employeeService.findAll(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(employee), PageRequest.of(0, 50), 1));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getEmployees_passesCountryFilterToService() throws Exception {
        when(employeeService.findAll(eq("IN"), any(), any()))
                .thenReturn(new PageImpl<>(List.of(employee)));

        mockMvc.perform(get("/api/employees").param("country", "IN"))
                .andExpect(status().isOk());

        verify(employeeService).findAll(eq("IN"), any(), any());
    }

    @Test
    void getEmployeeById_returns200WhenFound() throws Exception {
        when(employeeService.findById(employee.getId())).thenReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void getEmployeeById_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(employeeService.findById(id)).thenThrow(new EmployeeNotFoundException(id));

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void createEmployee_returns201WithBody() throws Exception {
        when(employeeService.create(any(Employee.class))).thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"));
    }

    @Test
    void createEmployee_returns400OnInvalidBody() throws Exception {
        EmployeeRequest invalid = new EmployeeRequest(
                "", "Engineer", "IN",
                new BigDecimal("50000"), "INR", "jane@example.com",
                "Engineering", LocalDate.now().minusDays(1)
        );

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void updateEmployee_returns200WithUpdatedBody() throws Exception {
        when(employeeService.update(eq(employee.getId()), any(Employee.class))).thenReturn(employee);

        mockMvc.perform(put("/api/employees/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(employee.getId().toString()));
    }

    @Test
    void deleteEmployee_returns200WithMessage() throws Exception {
        doNothing().when(employeeService).delete(employee.getId());

        mockMvc.perform(delete("/api/employees/{id}", employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deleted successfully"));

        verify(employeeService).delete(employee.getId());
    }

    @Test
    void deleteEmployee_returns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new EmployeeNotFoundException(id)).when(employeeService).delete(id);

        mockMvc.perform(delete("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }
}
