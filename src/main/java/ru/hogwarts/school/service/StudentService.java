package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final Map<Long, Student> students = new HashMap<>();
    private long generateStudentId = 1;

    public Collection<Student> getAll() {
        return students.values();
    }

    public Student addStudent(Student student) {
        student.setId(generateStudentId++);
        students.put(student.getId(), student);
        return student;
    }

    public Student findStudent(Long studentId) {
        return students.get(studentId);
    }

    public Student editStudent(Student student) {
        if (!students.containsKey(student.getId())) {
            return null;
        }
        students.put(student.getId(), student);
        return student;
    }

    public Student deleteStudent(Long studentId) {
        return students.remove(studentId);
    }

    public Collection<Student> getStudentsByAge(int age) {
        return students.values()
                .stream()
                .filter(e -> e.getAge() == age)
                .collect(Collectors.toList());
    }
}
