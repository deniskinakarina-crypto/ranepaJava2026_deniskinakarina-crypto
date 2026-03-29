package ru.ranepa.repository;

import ru.ranepa.model.Employee;

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
}