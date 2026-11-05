package com.example.studentapi.controller;

import com.example.studentapi.model.Course;
import com.example.studentapi.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id);
        }
        return ResponseEntity.ok(course.get());
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        String error = validateCourse(course);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        course.setId(null);
        Course saved = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Optional<Course> existingOpt = courseRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id);
        }

        String error = validateCourse(course);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        Course existing = existingOpt.get();
        existing.setCourseName(course.getCourseName());
        existing.setDuration(course.getDuration());
        existing.setFees(course.getFees());
        return ResponseEntity.ok(courseRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        if (!courseRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        return ResponseEntity.ok("Course deleted with id: " + id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String name) {
        return ResponseEntity.ok(courseRepository.findByCourseNameContainingIgnoreCase(name));
    }

    private String validateCourse(Course course) {
        if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            return "courseName is required";
        }
        if (course.getDuration() == null || course.getDuration().isBlank()) {
            return "duration is required";
        }
        if (course.getFees() == null || course.getFees() < 0) {
            return "fees must be a non-negative number";
        }
        return null;
    }
}
