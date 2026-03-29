package ru.ranepa.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;
import ru.ranepa.repository.EmployeeRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository repository;

    @BeforeEach
    void setUp() {
        repository = new EmployeeRepositoryImpl();
        employeeService = new EmployeeService(repository);
    }

    @Test
    void shouldCalculateAverageSalary() {
        repository.save(new Employee("Danila Poperechny", "Developer",
                BigDecimal.valueOf(95000), LocalDate.now()));
        repository.save(new Employee("Vyacheslav Komisarenko", "Manager",
                BigDecimal.valueOf(50000), LocalDate.now()));
        repository.save(new Employee("Sergey Orlov", "QA",
                BigDecimal.valueOf(45000), LocalDate.now()));

        BigDecimal result = employeeService.calculateAverageSalary();

        assertEquals(BigDecimal.valueOf(63333.33), result);
    }

    @Test
    void shouldFindHighestPaidEmployee() {
        repository.save(new Employee("Danila Poperechny", "Developer",
                BigDecimal.valueOf(95000), LocalDate.now()));
        repository.save(new Employee("Vyacheslav Komisarenko", "Manager",
                BigDecimal.valueOf(50000), LocalDate.now()));

        var result = employeeService.findHighestPaidEmployee();

        assertTrue(result.isPresent());
        assertEquals("Danila Poperechny", result.get().getName());
        assertEquals(BigDecimal.valueOf(95000), result.get().getSalary());
    }

    @Test
    void shouldReturnZeroForEmptyList() {
        BigDecimal result = employeeService.calculateAverageSalary();
        assertEquals(BigDecimal.ZERO, result);
    }
}