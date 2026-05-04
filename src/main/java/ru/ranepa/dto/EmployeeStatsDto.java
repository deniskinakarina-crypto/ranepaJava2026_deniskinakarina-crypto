package ru.ranepa.dto;

import java.math.BigDecimal;

public record EmployeeStatsDto(
        long totalCount,
        BigDecimal averageSalary,
        EmployeeResponseDto highestPaidEmployee
) {
    public static EmployeeStatsDto empty() {
        return new EmployeeStatsDto(0, BigDecimal.ZERO, null);
    }
}