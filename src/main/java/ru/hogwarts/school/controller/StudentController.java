package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.addStudent(student));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudent(@PathVariable("studentId") Long studentId) {
        Student foundStudent = studentService.findStudent(studentId);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @GetMapping("/all")
    public Collection<Student> findAllStudents(@RequestParam(required = false) Integer age,
                                               @RequestParam(required = false) Integer age2) {
        if (age != null && age2 != null && age > 0 && age2 > age) {
            return studentService.findByAgeBetween(age, age2);
        }
        return studentService.getAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable("id") Long id, @RequestBody Student student) {
        Student updatedStudent = studentService.editStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("{studentId}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok().build();
        // or ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> filterStudentsByAge(@RequestParam("age") int age) {
        if (age > 0){
            return ResponseEntity.ok(studentService.getStudentsByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}
