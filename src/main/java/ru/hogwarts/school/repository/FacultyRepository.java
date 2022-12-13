package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Collection<Faculty> findFacultiesByNameContainsIgnoreCase(String name);
    Collection<Faculty> findFacultiesByColorIgnoreCase(String color);

    @Query("SELECT * FROM student WHERE faculty_id = id")
    Collection<Student> findStudentsInFaculty(Integer id);

}
