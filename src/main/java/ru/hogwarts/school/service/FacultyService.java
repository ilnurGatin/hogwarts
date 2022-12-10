package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private long generatedFacultyId = 1;
    private final Map<Long, Faculty> faculties = new HashMap<>();

    public Collection<Faculty> getAll() {
        return faculties.values();
    }

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(generatedFacultyId++);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty findFaculty(Long facultyId) {
        return faculties.get(facultyId);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) {
            return null;
        }
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty deleteFaculty(Long facultyId) {
        return faculties.remove(facultyId);
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        return faculties.values()
                .stream()
                .filter(e -> e.getColor().equals(color))
                .collect(Collectors.toList());
    }
}
