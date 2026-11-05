package com.example.studentapi;

import com.example.studentapi.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourseApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void courseCrudAndSearchWork() {
        Course payload = new Course("Java Advanced", "4 months", 20000.0);
        ResponseEntity<Course> created = restTemplate.postForEntity("/api/courses", payload, Course.class);
        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        Long id = created.getBody().getId();

        ResponseEntity<Course> fetched = restTemplate.getForEntity("/api/courses/" + id, Course.class);
        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertEquals("Java Advanced", fetched.getBody().getCourseName());

        Course update = new Course("Java Masterclass", "5 months", 25000.0);
        ResponseEntity<Course> updated = restTemplate.exchange(
                "/api/courses/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(update),
                Course.class
        );
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertEquals("Java Masterclass", updated.getBody().getCourseName());

        ResponseEntity<Course[]> search = restTemplate.getForEntity("/api/courses/search?name=java", Course[].class);
        assertEquals(HttpStatus.OK, search.getStatusCode());
        assertTrue(search.getBody().length >= 1);

        ResponseEntity<String> missing = restTemplate.getForEntity("/api/courses/99999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());

        restTemplate.delete("/api/courses/" + id);
        ResponseEntity<String> afterDelete = restTemplate.getForEntity("/api/courses/" + id, String.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDelete.getStatusCode());
    }
}
