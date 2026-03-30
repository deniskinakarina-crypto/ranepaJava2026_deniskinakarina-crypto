package ru.ranepa.service;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public BigDecimal calculateAverageSalary() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumSalary = BigDecimal.ZERO;
        for (Employee employee : allEmployees) {
            sumSalary = sumSalary.add(employee.getSalary());
        }

        return sumSalary.divide(
                BigDecimal.valueOf(allEmployees.size()),
                2,
                RoundingMode.HALF_UP
        );
    }

    public Optional<Employee> findHighestPaidEmployee() {
        List<Employee> allEmployees = employeeRepository.findAll();

        if (allEmployees.isEmpty()) {
            return Optional.empty();
        }

        Employee highestPaid = allEmployees.get(0);
        for (Employee employee : allEmployees) {
            if (employee.getSalary().compareTo(highestPaid.getSalary()) > 0) {
                highestPaid = employee;
            }
        }
        return Optional.of(highestPaid);
    }

    public List<Employee> filterByPosition(String position) {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> result = new ArrayList<>();

        for (Employee employee : allEmployees) {
            if (employee.getPosition().equalsIgnoreCase(position)) {
                result.add(employee);
            }
        }
        return result;
    }

    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public String addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public String deleteEmployee(Long id) {
        return employeeRepository.delete(id);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    //Сортировка
    public List<Employee> sortByName() {
        List<Employee> employees = employeeRepository.findAll();
        employees.sort(Comparator.comparing(Employee::getName));
        return employees;
    }

    public void saveToFile(String filename) {
        List<Employee> employees = employeeRepository.findAll();

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("ID,Name,Position,Salary,Hire Date\n");

            for (Employee employee : employees) {
                writer.write(
                        employee.getId() + "," +
                                employee.getName() + "," +
                                employee.getPosition() + "," +
                                employee.getSalary() + "," +
                                employee.getHireDate() + "\n"
                );
            }
            System.out.println("Данные сохранены в файл: " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
    public void loadData(String filename) {
        employeeRepository.loadFromFile(filename);
    }
}