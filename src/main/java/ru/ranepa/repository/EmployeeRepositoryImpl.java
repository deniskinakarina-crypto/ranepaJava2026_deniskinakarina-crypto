package ru.ranepa.repository;

import ru.ranepa.model.Employee;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final Map<Long, Employee> employees = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public synchronized String save(Employee employee) {
        employee.setId(nextId);
        employees.put(nextId, employee);
        nextId++;
        return "Employee " + employee.getId() + " was saved successfully";
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public synchronized String delete(Long id) {
        if (employees.remove(id) != null) {
            return "Employee " + id + " was deleted successfully";
        }
        return "Employee with id " + id + " not found";
    }

    @Override
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Пропускаем заголовок (первая строка)

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 5) {
                    try {
                        Long id = Long.parseLong(parts[0].trim());
                        String name = parts[1].trim();
                        String position = parts[2].trim();
                        BigDecimal salary = new BigDecimal(parts[3].trim());
                        LocalDate hireDate = LocalDate.parse(parts[4].trim(), DateTimeFormatter.ISO_LOCAL_DATE);

                        Employee employee = new Employee(name, position, salary, hireDate);
                        employee.setId(id);

                        employees.put(id, employee);

                        if (id >= nextId) {
                            nextId = id + 1;
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + employees.size() + " employees from file.");
        } catch (IOException e) {
            System.out.println("No existing data file found. Starting with empty database.");
        }
    }
}