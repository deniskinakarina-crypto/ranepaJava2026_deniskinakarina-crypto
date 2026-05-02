package ru.ranepa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequestDto(
        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotBlank(message = "Position cannot be empty")
        String position,

        @NotNull(message = "Salary cannot be null")
        @Positive(message = "Salary must be positive")
        BigDecimal salary,

        @NotNull(message = "Hire date cannot be null")
        LocalDate hireDate
) {
}
