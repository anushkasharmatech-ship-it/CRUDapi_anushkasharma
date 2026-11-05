package com.example.studentapi.controller;

import com.example.studentapi.model.Student;
import com.example.studentapi.model.Teacher;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public StudentController(StudentRepository studentRepository,
                             TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/getById")
    public ResponseEntity<?> getStudentById(@RequestParam Long id) {
        return studentRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student) {
        if (student.getTeacher() != null && student.getTeacher().getId() == null) {
            return ResponseEntity.badRequest().body("teacher.id is required when a teacher is provided");
        }
        if (student.getTeacher() != null) {
            Optional<Teacher> teacher = teacherRepository.findById(student.getTeacher().getId());
            if (teacher.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Teacher not found with id: " + student.getTeacher().getId());
            }
            student.setTeacher(teacher.get());
        }
        student.setId(null);
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        Optional<Student> existingOptional = studentRepository.findById(id);
        if (existingOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found with id: " + id);
        }

        Student existing = existingOptional.get();
        existing.setName(student.getName());
        existing.setEmail(student.getEmail());
        existing.setAge(student.getAge());
        if (student.getTeacher() != null && student.getTeacher().getId() == null) {
            return ResponseEntity.badRequest().body("teacher.id is required when a teacher is provided");
        }
        if (student.getTeacher() != null) {
            Optional<Teacher> teacher = teacherRepository.findById(student.getTeacher().getId());
            if (teacher.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Teacher not found with id: " + student.getTeacher().getId());
            }
            existing.setTeacher(teacher.get());
        }
        return ResponseEntity.ok(studentRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
        return ResponseEntity.ok("Student deleted with id: " + id);
    }

    @GetMapping("/by-teacher")
    public List<Student> getStudentsByTeacherName(@RequestParam String name) {
        return studentRepository.findStudentsByTeacherName(name);
    }

    @GetMapping("/teacher-counts")
    public List<Object[]> countStudentsPerTeacher() {
        return studentRepository.countStudentsPerTeacher();
    }
}
