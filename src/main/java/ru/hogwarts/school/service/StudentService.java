package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public Collection<Student> getAll() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for add student");
        return studentRepository.save(student);
    }

    public Student findStudent(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
    }

    public Student editStudent(Long id, Student student) {
        logger.info("Was invoked method for edit student");
        Student foundStudent = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        foundStudent.setAge(student.getAge());
        foundStudent.setName(student.getName());
        return studentRepository.save(foundStudent);
    }

    public void deleteStudent(Long studentId) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(studentId);
    }

    public Collection<Student> getStudentsByAge(int age) {
        logger.info("Was invoked method looking for students by age: {}", age);
        return studentRepository.findAll()
                .stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> findByAgeBetween(Integer age, Integer age2) {
        logger.info("Was invoked method looking for students between ages: {} and {}", age, age2);
        return studentRepository.findStudentsByAgeBetween(age, age2);
    }

    public Faculty getStudentsFaculty(Long id) {
        logger.info("Was invoked method looking for student's faculty by student id: {}", id);
        return studentRepository.findById(id).orElseThrow(StudentNotFoundException::new).getFaculty();
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Was invoked method for search for student's avatar");
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }

    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for uploading student's avatar");
        Student student = findStudent(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public Integer countStudents() {
        return studentRepository.countStudents();
    }

    public Integer findAverageAge() {
        return studentRepository.findAverageAge();
    }

    public Collection<Student> getLastFiveStudents() {
        return studentRepository.getLastFiveStudents();
    }
}
