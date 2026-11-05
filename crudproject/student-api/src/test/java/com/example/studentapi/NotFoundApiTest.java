package com.example.studentapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.example.studentapi.model.Student;
import com.example.studentapi.model.Teacher;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotFoundApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void missingStudentAndTeacherReturnNotFoundInsteadOfServerError() {
        ResponseEntity<String> missingStudent = restTemplate.getForEntity(
                "/api/students/getById?id=999999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, missingStudent.getStatusCode());
        assertEquals("Student not found with id: 999999", missingStudent.getBody());

        ResponseEntity<String> missingTeacher = restTemplate.getForEntity(
                "/api/teachers/999999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, missingTeacher.getStatusCode());
        assertEquals("Teacher not found with id: 999999", missingTeacher.getBody());
    }

    @Test
    void updatingMissingStudentOrTeacherReturnsNotFound() {
        Student student = new Student("Test", "test@example.com", 20);
        ResponseEntity<String> studentResponse = restTemplate.exchange(
                "/api/students/999999", HttpMethod.PUT,
                new HttpEntity<>(student), String.class);
        assertEquals(HttpStatus.NOT_FOUND, studentResponse.getStatusCode());

        Teacher teacher = new Teacher("Test Teacher", "Math");
        ResponseEntity<String> teacherResponse = restTemplate.exchange(
                "/api/teachers/999999", HttpMethod.PUT,
                new HttpEntity<>(teacher), String.class);
        assertEquals(HttpStatus.NOT_FOUND, teacherResponse.getStatusCode());
    }
}
