package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Collection<Student> getAll() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
    }

    public Student editStudent(Long id, Student student) {
        Student foundStudent = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        foundStudent.setAge(student.getAge());
        foundStudent.setName(student.getName());
        return studentRepository.save(foundStudent);
    }

    public void deleteStudent(Long studentId) {
        studentRepository.deleteById(studentId);
    }

    public Collection<Student> getStudentsByAge(int age) {
        return studentRepository.findAll()
                .stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
    }

    public Collection<Student> findByAgeBetween(Integer age, Integer age2) {
        return studentRepository.findStudentsByAgeBetween(age, age2);
    }

    public Faculty getStudentsFaculty(Long id) {
        return studentRepository.findById(id).orElseThrow(StudentNotFoundException::new).getFaculty();
    }
}
