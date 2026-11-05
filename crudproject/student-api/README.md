# Student API

A beginner-friendly Spring Boot REST API. It already had **Student** and **Teacher** CRUD. This project now also has a **Course** CRUD module.

The app uses an **H2 in-memory database**, so you can run it without installing MySQL.

## Technologies used

- Java 21
- Spring Boot 3
- Spring Web (REST APIs)
- Spring Data JPA (database access)
- H2 in-memory database
- Maven

## Project architecture (keep this in mind for viva)

```
Controller  -->  Repository  -->  Database table
```

- **Model / Entity**: a Java class mapped to a database table (`@Entity`)
- **Repository**: talks to the database (`JpaRepository`)
- **Controller**: exposes HTTP endpoints (`@RestController`)

Existing packages were reused:

- `com.example.studentapi.model`
- `com.example.studentapi.repository`
- `com.example.studentapi.controller`

## Database configuration

File: `src/main/resources/application.properties`

- Database: H2 in-memory
- JDBC URL: `jdbc:h2:mem:studentdb`
- Username: `sa`
- Password: (leave empty)
- Tables are created automatically by Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
- Sample rows are loaded from `src/main/resources/data.sql` on startup
- Data stays in memory **while the app is running**. Restarting the app reloads the sample data.

H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

1. JDBC URL: `jdbc:h2:mem:studentdb`
2. User Name: `sa`
3. Password: leave blank
4. Click **Connect**

## Steps to run

1. Install **Java 21** and **Maven**.
2. Open a terminal in the project folder (`student-api` that contains `pom.xml`):

```bash
mvn spring-boot:run
```

3. Wait until you see that Tomcat started on port **8080**.
4. Open [http://localhost:8080/api/courses](http://localhost:8080/api/courses) in a browser. You should see sample courses.

Stop the app with `Ctrl + C`.

Build and run the automated tests without starting a persistent server:

```bash
mvn test
```

The tests cover Course CRUD/search and verify that missing Student and Teacher records return HTTP 404.

## Sample data on startup

Teachers, students and courses are inserted from `data.sql`:

- Teachers: Mrs. Sharma, Mr. Verma, Ms. Iyer
- Students: Alice, Bob, Charlie, Diya
- Courses: Java Programming, Spring Boot, Python Basics

## API endpoints

### Teachers

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/teachers` | Get all teachers |
| GET | `/api/teachers/{id}` | Get teacher by id (404 if missing) |
| POST | `/api/teachers` | Create a teacher |
| PUT | `/api/teachers/{id}` | Update a teacher (404 if missing) |
| DELETE | `/api/teachers/{id}` | Delete a teacher (404 if missing) |

**Create / update teacher JSON**

```json
{
  "name": "Mr. Khan",
  "subject": "History"
}
```

### Students

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | Get all students |
| GET | `/api/students/getById?id=1` | Get student by id (404 if missing) |
| POST | `/api/students` | Create a student |
| PUT | `/api/students/{id}` | Update a student (404 if missing) |
| DELETE | `/api/students/{id}` | Delete a student (404 if missing) |
| GET | `/api/students/by-teacher?name=Mrs. Sharma` | Students of a teacher |
| GET | `/api/students/teacher-counts` | Count students per teacher |

**Create / update student JSON**

```json
{
  "name": "Esha",
  "email": "esha@example.com",
  "age": 22,
  "teacher": { "id": 1 }
}
```

`teacher.id` must already exist.

### Courses (new)

| Method | Endpoint | Status codes |
|--------|----------|----------------|
| GET | `/api/courses` | 200 |
| GET | `/api/courses/{id}` | 200, or 404 if missing |
| POST | `/api/courses` | 201 created, or 400 if invalid |
| PUT | `/api/courses/{id}` | 200, 400, or 404 |
| DELETE | `/api/courses/{id}` | 200, or 404 if missing |
| GET | `/api/courses/search?name=Java` | 200, case-insensitive partial match |

**Create / update course JSON**

```json
{
  "courseName": "Data Structures",
  "duration": "3 months",
  "fees": 16000
}
```

Validation:

- `courseName` is required
- `duration` is required
- `fees` must be present and cannot be negative

Missing student, teacher, or course records return **404 Not Found** rather than an internal server error. For example, `GET /api/students/getById?id=99999` and `GET /api/teachers/99999` return 404.

## Postman testing instructions

Postman desktop is optional. The automated collection and local environment template are in:

- `postman/Student_API_Collection.json`
- `postman/Student_API_Environment.json`

The environment file contains only the local `baseUrl` and no credentials.

1. Start the Spring Boot app.
2. Open Postman → **Import** → select that JSON file.
3. Run requests in this order:
   - GET all teachers / students / courses
   - POST a new course
   - GET that course by id
   - PUT to update it
   - GET `/api/courses/search?name=Java`
   - GET an invalid id (`99999`) and confirm 404
   - DELETE a course
4. You can also test with `curl` if Postman is not installed. Examples:

```bash
curl http://localhost:8080/api/courses
curl http://localhost:8080/api/students
curl http://localhost:8080/api/teachers
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d "{\"courseName\":\"SQL\",\"duration\":\"1 month\",\"fees\":8000}"
curl http://localhost:8080/api/courses/search?name=Java
```

## Viva notes (simple language)

- **`@Entity`**: this class becomes a database table.
- **`@Id` + `@GeneratedValue`**: database creates the primary key automatically.
- **`JpaRepository<Course, Long>`**: Spring gives ready methods like `findAll()`, `findById()`, `save()`, `deleteById()`.
- **`findByCourseNameContainingIgnoreCase`**: Spring Data builds a `LIKE` search from the method name. No SQL needed.
- **Constructor injection**: the controller receives the repository in its constructor. Spring creates both objects and connects them.
- **`ResponseEntity`**: lets us return both the body and the HTTP status (201, 404, 400).
- **Student → Teacher**: many students can belong to one teacher (`@ManyToOne`). The foreign key column is `teacher_id`.
- **H2 vs MySQL**: H2 runs in RAM, so the project starts on any laptop. MySQL needs a local server and a created database.

## Original MySQL settings (not used now)

The teacher project originally pointed to MySQL:

```
jdbc:mysql://127.0.0.1:3306/SHIKHAR_BATCH
```

That was replaced with H2 so the app starts without MySQL. The MySQL driver is still in `pom.xml` if you ever switch back.
