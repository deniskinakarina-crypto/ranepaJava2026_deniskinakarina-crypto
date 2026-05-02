package ru.ranepa.dto;

import ru.ranepa.model.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeResponseDto(
        Long id,
        String name,
        String position,
        BigDecimal salary,
        LocalDate hireDate,
        LocalDateTime createdAt
) {
    public static EmployeeResponseDto fromEntity(Employee employee) {
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getName(),
                employee.getPosition(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getCreatedAt()
        );
    }
}