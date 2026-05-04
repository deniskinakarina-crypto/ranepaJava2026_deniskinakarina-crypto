package ru.ranepa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.exception.EmployeeNotFoundException;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private EmployeeRequestDto testRequestDto;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(
                "Ivan Ivanov",
                "Java Developer",
                BigDecimal.valueOf(150000),
                LocalDate.of(2024, 1, 15)
        );

        testRequestDto = new EmployeeRequestDto(
                "Ivan Ivanov",
                "Java Developer",
                BigDecimal.valueOf(150000),
                LocalDate.of(2024, 1, 15)
        );
    }

    @Test
    void shouldCreateEmployee() {
        given(employeeRepository.save(any(Employee.class)))
                .willReturn(testEmployee);

        EmployeeResponseDto result = employeeService.createEmployee(testRequestDto);

        assertNotNull(result);
        assertEquals("Ivan Ivanov", result.name());
        assertEquals("Java Developer", result.position());

        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldGetAllEmployees() {
        List<Employee> employees = List.of(testEmployee);
        given(employeeRepository.findAll()).willReturn(employees);

        List<EmployeeResponseDto> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ivan Ivanov", result.get(0).name());
    }

    @Test
    void shouldGetEmployeeById() {
        Long id = 1L;
        given(employeeRepository.findById(id)).willReturn(Optional.of(testEmployee));

        EmployeeResponseDto result = employeeService.getEmployeeById(id);

        assertNotNull(result);
        assertEquals("Ivan Ivanov", result.name());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        Long id = 999L;
        given(employeeRepository.findById(id)).willReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () ->
                employeeService.getEmployeeById(id)
        );
    }

    @Test
    void shouldDeleteEmployee() {
        Long id = 1L;
        given(employeeRepository.existsById(id)).willReturn(true);

        employeeService.deleteEmployee(id);

        verify(employeeRepository).deleteById(id);
    }

    @Test
    void shouldGetStatistics() {
        Employee emp1 = new Employee("Ivan", "Dev",
                BigDecimal.valueOf(100000), LocalDate.now());
        Employee emp2 = new Employee("Anna", "Manager",
                BigDecimal.valueOf(200000), LocalDate.now());

        List<Employee> employees = List.of(emp1, emp2);
        given(employeeRepository.findAll()).willReturn(employees);

        EmployeeStatsDto stats = employeeService.getStatistics();

        assertNotNull(stats);
        assertEquals(2, stats.totalCount());
        assertEquals(BigDecimal.valueOf(150000), stats.averageSalary());
        assertEquals("Anna", stats.highestPaidEmployee().name());
    }

    @Test
    void shouldReturnEmptyStatsForEmptyDatabase() {
        given(employeeRepository.findAll()).willReturn(List.of());

        EmployeeStatsDto stats = employeeService.getStatistics();

        assertNotNull(stats);
        assertEquals(0, stats.totalCount());
        assertEquals(BigDecimal.ZERO, stats.averageSalary());
        assertNull(stats.highestPaidEmployee());
    }

    @Test
    void shouldGetEmployeesByPosition() {
        Employee dev1 = new Employee("Ivan", "Developer",
                BigDecimal.valueOf(150000), LocalDate.now());
        Employee dev2 = new Employee("Anna", "Developer",
                BigDecimal.valueOf(160000), LocalDate.now());

        given(employeeRepository.findByPositionIgnoreCase("Developer"))
                .willReturn(List.of(dev1, dev2));

        List<EmployeeResponseDto> result = employeeService.getEmployeesByPosition("Developer");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Developer", result.get(0).position());
    }

    @Test
    void shouldUpdateEmployee() {
        Long id = 1L;
        EmployeeRequestDto updateRequest = new EmployeeRequestDto(
                "Ivan Ivanov",
                "Senior Developer",
                BigDecimal.valueOf(200000),
                LocalDate.of(2024, 1, 15)
        );

        given(employeeRepository.findById(id)).willReturn(Optional.of(testEmployee));
        given(employeeRepository.save(any(Employee.class))).willReturn(testEmployee);

        EmployeeResponseDto result = employeeService.updateEmployee(id, updateRequest);

        assertNotNull(result);
        verify(employeeRepository).save(testEmployee);
    }
}