# Практическое задание: Разработка системы управления персоналом (HRM Lite) на Spring Boot

## 1. Общее описание

**Цель задания:** Закрепить навыки объектно-ориентированного программирования (ООП), работы с коллекциями, Spring Boot, Spring Data JPA и REST API. Разработать веб-приложение для управления данными сотрудников компании с сохранением в базу данных.

Система должна позволять через REST API добавлять, удалять, искать и просматривать список сотрудников. Данные хранятся в реляционной базе данных (H2 для разработки).

## 2. Технологический стек

* **Язык:** Java 21
* **Фреймворк:** Spring Boot 3.2+
* **База данных:** H2 (встроенная) + Spring Data JPA
* **Веб:** Spring Web (REST API)
* **Тестирование:** JUnit 5 + Spring Boot Test

## 3. Архитектура приложения (Layered Architecture)

Проект должен следовать принципам чистой архитектуры Spring Boot:

```
src/main/java/com/example/hrm/
├── model          — JPA сущности (Employee)
├── repository     — Spring Data JPA репозитории
├── service        — бизнес-логика (@Service)
├── controller     — REST контроллеры (@RestController)
├── dto            — Data Transfer Objects для API
└── HrmApplication — точка входа (@SpringBootApplication)
```

## 4. Подробные требования к реализации

### Шаг 1: Модель данных (`Employee` Entity)

Измените класс `Employee` сделав из него **JPA-сущность** с аннотациями `@Entity`, `@Table`:

* `Long id` — `@Id @GeneratedValue`
* `String name`
* `String position`
* `BigDecimal salary` — используйте BigDecimal вместо double
* `LocalDate hireDate`
* `@PrePersist` — автоматически устанавливать дату создания

### Шаг 2: Репозиторий (`EmployeeRepository`)

Реализуйте в вашем репозитории интерфейс `JpaRepository<Employee, Long>`
Добавьте дополнительные методы. 
* Дополнительные методы поиска:
  * `findByPosition(String position)`
  * `findBySalaryGreaterThanEqual(BigDecimal salary)`

### Шаг 3: Бизнес-логика (`EmployeeService`)
Отметьте `EmployeeService` как компонент при помощи аннотации `@Service`. 
Добавьте поле `EmployeeRepository` и укажите его в конструкторе. 
Переделайте методы так, чтобы они ссылались на методы репозитория. 

### Шаг 4: REST API (`EmployeeController`)
В пакете Controller добавьте класс `EmployeeController` с зависимостью на `EmployeeService`.
Реализуйте REST endpoints:

```
GET     /api/employees           — все сотрудники
GET     /api/employees/{id}      — сотрудник по ID
POST    /api/employees           — создать сотрудника
DELETE  /api/employees/{id}      — удалить
GET     /api/employees/position/{position}  — по должности
GET     /api/employees/stats     — статистика (средняя ЗП, топ)
```

### Шаг 5: DTO классы

Создайте:
* `EmployeeRequestDto` — для создания (без ID)
* `EmployeeResponseDto` — для ответа (с ID и всеми полями)
* `EmployeeStatsDto` — для статистики

## 5. Конфигурация Spring Boot

### application.properties:
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:hrm
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

# JSON настройки
spring.jackson.date-format=yyyy-MM-dd
```

## 6. Swagger/OpenAPI документация

Добавьте зависимость `springdoc-openapi-starter-webmvc-ui` и настройте автоматическую документацию API по адресу `/swagger-ui.html`.

## 7. Интеграционное тестирование

Напишите **интеграционные тесты** для контроллеров:

```java
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldReturnAllEmployees() throws Exception {
        // Given-When-Then с использованием MockMvc
    }
}
```

**Что тестировать:**
1. Создание сотрудника (POST 201 Created)
2. Получение списка (GET 200 OK)
3. Получение несуществующего (GET 404 Not Found)
4. Статистику при пустой БД

## 8. Swagger пример запросов

После запуска студенты должны протестировать API через Swagger:

```
POST /api/employees
{
  "name": "Иван Иванов",
  "position": "Java Developer", 
  "salary": 150000.00,
  "hireDate": "2024-01-15"
}
```

## 9. Критерии оценки (Definition of Done)

### Обязательно (уровень 1/3):
1. ✅ Spring Boot проект запускается
2. ✅ CRUD операции через REST API работают
3. ✅ H2 база данных создается автоматически
4. ✅ Swagger документация доступна

### Желательно (уровень 2/3):
1. ✅ DTO классы вместо прямой работы с Entity
2. ✅ Обработка ошибок (@Valid, @ExceptionHandler)
3. ✅ Интеграционные тесты (3+ теста)

### Продвинутый уровень (3/3):
1. ✅ Сортировка (`Pageable` в репозитории)
2. ✅ Валидация (`@NotNull`, `@Positive`)
3. ✅ Кастомные исключения и GlobalExceptionHandler

Проверить:
- `http://localhost:8080/h2-console` (база данных)
- `http://localhost:8080/swagger-ui.html` (API)
