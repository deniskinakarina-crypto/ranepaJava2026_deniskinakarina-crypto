package ru.ranepa.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.exception.EmployeeNotFoundException;
import ru.ranepa.hrm.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void shouldReturnAllEmployees() throws Exception {
        List<EmployeeResponseDto> employees = List.of(
                new EmployeeResponseDto(1L, "Ivan Ivanov", "Developer",
                        BigDecimal.valueOf(150000), LocalDate.of(2024, 1, 15), null)
        );
        given(employeeService.getAllEmployees()).willReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ivan Ivanov"))
                .andExpect(jsonPath("$[0].position").value("Developer"))
                .andExpect(jsonPath("$[0].salary").value(150000));
    }

    @Test
    void shouldReturnEmployeeById() throws Exception {
        EmployeeResponseDto employee = new EmployeeResponseDto(
                1L, "Anna Petrova", "Manager",
                BigDecimal.valueOf(200000), LocalDate.of(2024, 2, 1), null
        );
        given(employeeService.getEmployeeById(1L)).willReturn(employee);

        mockMvc.perform(get("/api/employees/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Anna Petrova"))
                .andExpect(jsonPath("$.position").value("Manager"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentEmployee() throws Exception {
        given(employeeService.getEmployeeById(999L))
                .willThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(get("/api/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeRequestDto request = new EmployeeRequestDto(
                "Oleg Sidorov", "QA Engineer",
                BigDecimal.valueOf(120000), LocalDate.of(2024, 3, 1)
        );
        EmployeeResponseDto response = new EmployeeResponseDto(
                1L, "Oleg Sidorov", "QA Engineer",
                BigDecimal.valueOf(120000), LocalDate.of(2024, 3, 1), null
        );
        given(employeeService.createEmployee(any(EmployeeRequestDto.class))).willReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Oleg Sidorov",
                                    "position": "QA Engineer",
                                    "salary": 120000,
                                    "hireDate": "2024-03-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Oleg Sidorov"))
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldDeleteEmployee() throws Exception {
        willDoNothing().given(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnStatistics() throws Exception {
        EmployeeResponseDto highestPaid = new EmployeeResponseDto(
                1L, "Top Manager", "CEO",
                BigDecimal.valueOf(500000), LocalDate.of(2020, 1, 1), null
        );
        EmployeeStatsDto stats = new EmployeeStatsDto(
                10, BigDecimal.valueOf(150000), highestPaid
        );
        given(employeeService.getStatistics()).willReturn(stats);

        mockMvc.perform(get("/api/employees/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(10))
                .andExpect(jsonPath("$.averageSalary").value(150000))
                .andExpect(jsonPath("$.highestPaidEmployee.name").value("Top Manager"));
    }

    @Test
    void shouldReturnEmptyStatsForEmptyDatabase() throws Exception {
        EmployeeStatsDto emptyStats = EmployeeStatsDto.empty();
        given(employeeService.getStatistics()).willReturn(emptyStats);

        mockMvc.perform(get("/api/employees/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(0))
                .andExpect(jsonPath("$.averageSalary").value(0))
                .andExpect(jsonPath("$.highestPaidEmployee").doesNotExist());
    }

    @Test
    void shouldReturnEmployeesByPosition() throws Exception {
        List<EmployeeResponseDto> developers = List.of(
                new EmployeeResponseDto(1L, "Ivan", "Developer",
                        BigDecimal.valueOf(150000), LocalDate.of(2024, 1, 15), null),
                new EmployeeResponseDto(2L, "Anna", "Developer",
                        BigDecimal.valueOf(160000), LocalDate.of(2024, 2, 1), null)
        );
        given(employeeService.getEmployeesByPosition("Developer")).willReturn(developers);

        mockMvc.perform(get("/api/employees/position/{position}", "Developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].position").value("Developer"))
                .andExpect(jsonPath("$[1].position").value("Developer"));
    }

    @Test
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "position": "Developer",
                                    "salary": -100,
                                    "hireDate": "2024-01-01"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").exists());
    }
}