package ru.ranepa.repository;

import ru.ranepa.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    String save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    String delete(Long id);

    void loadFromFile(String filename);
}
