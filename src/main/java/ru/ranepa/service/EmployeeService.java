package ru.ranepa.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ranepa.dto.EmployeeRequestDto;
import ru.ranepa.dto.EmployeeResponseDto;
import ru.ranepa.dto.EmployeeStatsDto;
import ru.ranepa.exception.EmployeeNotFoundException;
import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        Employee employee = new Employee(
                request.name(),
                request.position(),
                request.salary(),
                request.hireDate()
        );
        Employee saved = employeeRepository.save(employee);
        return EmployeeResponseDto.fromEntity(saved);
    }

    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeResponseDto::fromEntity)
                .toList();
    }

    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return EmployeeResponseDto.fromEntity(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        employee.setPosition(request.position());
        employee.setSalary(request.salary());

        Employee updated = employeeRepository.save(employee);
        return EmployeeResponseDto.fromEntity(updated);
    }

    public List<EmployeeResponseDto> getEmployeesByPosition(String position) {
        return employeeRepository.findByPositionIgnoreCase(position)
                .stream()
                .map(EmployeeResponseDto::fromEntity)
                .toList();
    }

    public EmployeeStatsDto getStatistics() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            return EmployeeStatsDto.empty();
        }

        long totalCount = allEmployees.size();

        BigDecimal sumSalary = BigDecimal.ZERO;
        for (Employee employee : allEmployees) {
            sumSalary = sumSalary.add(employee.getSalary());
        }
        BigDecimal averageSalary = sumSalary.divide(
                BigDecimal.valueOf(totalCount),
                2,
                RoundingMode.HALF_UP
        );

        Employee highestPaid = allEmployees.get(0);
        for (Employee employee : allEmployees) {
            if (employee.getSalary().compareTo(highestPaid.getSalary()) > 0) {
                highestPaid = employee;
            }
        }

        return new EmployeeStatsDto(
                totalCount,
                averageSalary,
                EmployeeResponseDto.fromEntity(highestPaid)
        );
    }
}