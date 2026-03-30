package ru.ranepa;

import ru.ranepa.model.Employee;
import ru.ranepa.repository.EmployeeRepositoryImpl;
import ru.ranepa.service.EmployeeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class HrmApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeService employeeService;

    static {
        EmployeeRepositoryImpl repository = new EmployeeRepositoryImpl();
        employeeService = new EmployeeService(repository);

        // Загрузка данных из файла при запуске программы
        employeeService.loadData("employees.csv");
    }

    public static void main(String[] args) {
        System.out.println("=== Employee Menu ===");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showAllEmployees();
                    break;
                case "2":
                    addEmployee();
                    break;
                case "3":
                    deleteEmployee();
                    break;
                case "4":
                    findEmployee();
                    break;
                case "5":
                    showStatistics();
                    break;
                case "6":
                    sortByName();
                    break;
                case "7":
                    running = false;
                    System.out.println("Saving data to file...");
                    employeeService.saveToFile("employees.csv");
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid option, please try again");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("1. Show all employees");
        System.out.println("2. Add employee");
        System.out.println("3. Delete employee");
        System.out.println("4. Find employee by ID");
        System.out.println("5. Show statistics");
        System.out.println("6. Sort by name");
        System.out.println("7. Exit and save to file");
        System.out.print("Choose an option: ");
    }

    private static void showAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees found");
            return;
        }
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }

    private static void addEmployee() {
        try {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();

            System.out.print("Enter position: ");
            String position = scanner.nextLine();

            System.out.print("Enter salary: ");
            double salary = Double.parseDouble(scanner.nextLine());

            System.out.print("Enter hire date (YYYY-MM-DD): ");
            LocalDate hireDate = LocalDate.parse(
                    scanner.nextLine(),
                    DateTimeFormatter.ISO_LOCAL_DATE
            );

            Employee employee = new Employee(name, position, salary, hireDate);
            String result = employeeService.addEmployee(employee);
            System.out.println(result);

        } catch (NumberFormatException e) {
            System.out.println("Invalid salary format");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format, use YYYY-MM-DD");
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
        }
    }

    private static void deleteEmployee() {
        try {
            System.out.print("Enter employee ID to delete: ");
            Long id = Long.parseLong(scanner.nextLine());
            String result = employeeService.deleteEmployee(id);
            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format");
        }
    }

    private static void findEmployee() {
        try {
            System.out.print("Enter employee ID to find: ");
            Long id = Long.parseLong(scanner.nextLine());
            employeeService.findEmployeeById(id)
                    .ifPresentOrElse(
                            emp -> System.out.println("Found: " + emp),
                            () -> System.out.println("Employee not found")
                    );
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format");
        }
    }

    private static void showStatistics() {
        BigDecimal avgSalary = employeeService.calculateAverageSalary();
        System.out.println("Average salary: " + avgSalary);

        employeeService.findHighestPaidEmployee()
                .ifPresentOrElse(
                        emp -> System.out.println("Highest paid: " + emp),
                        () -> System.out.println("No employees to analyze")
                );
    }

    private static void sortByName() {
        List<Employee> sorted = employeeService.sortByName();
        if (sorted.isEmpty()) {
            System.out.println("No employees found");
            return;
        }
        System.out.println("Employees sorted by name");
        for (Employee employee : sorted) {
            System.out.println(employee);
        }
    }
}