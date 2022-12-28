package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

@RestController
@RequestMapping("/student")
public class StudentController {

    Logger logger = LoggerFactory.getLogger(StudentService.class);
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
            logger.error("Student with id: {} doesn't exist", studentId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentsFaculty(@PathVariable("id") Long id) {
        return ResponseEntity.ok(studentService.getStudentsFaculty(id));
    }

    @GetMapping("/all")
    public Collection<Student> findAllStudents(@RequestParam(required = false) Integer age,
                                               @RequestParam(required = false) Integer age2) {
        if (age != null && age2 != null && age > 0 && age2 > age) {
            return studentService.findByAgeBetween(age, age2);
        }
        return studentService.getAll();
    }

    @GetMapping("/all/count")
    public Integer countStudents() {
        return studentService.countStudents();
    }

    @GetMapping("/all/avg-age")
    public Integer findAverageAge() {
        return studentService.findAverageAge();
    }

    @GetMapping("/all/avg-age-stream")
    public OptionalDouble findAverageAgeWithStream() {
        return studentService.findAverageAgeWithStream();
    }

    @GetMapping("/all/student-name-char")
    public List<String> findStudentsWithNamesStartingOnA() {
        return studentService.findStudentsWithNamesStartingOnA();
    }

    @GetMapping("/all/limit")
    public Collection<Student> getLastFiveStudent() {
        return studentService.getLastFiveStudents();
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

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            logger.error("Chosen file was more than 300 kB");
            return ResponseEntity.badRequest().body("File is too big");
        }

        studentService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = studentService.findAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    public void downloadAvatar(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = studentService.findAvatar(id);

        Path path = Path.of(avatar.getFilePath());

        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream();) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }
}
