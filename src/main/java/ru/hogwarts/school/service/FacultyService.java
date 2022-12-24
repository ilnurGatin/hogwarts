package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;


import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Collection<Faculty> getAll() {
        logger.info("Was invoked method for getting all faculties");
        return facultyRepository.findAll();
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for adding a faculty {}", faculty.getName());
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long facultyId) {
        logger.info("Was invoked method for searching a faculty by id: {}", facultyId);
        return facultyRepository.findById(facultyId).orElseThrow(FacultyNotFoundException::new);
    }

    public Faculty editFaculty(Long id, Faculty faculty) {
        logger.info("Was invoked method for editing a faculty");
        Faculty foundFaculty = facultyRepository.findById(id).orElseThrow(FacultyNotFoundException::new);
        foundFaculty.setColor(faculty.getColor());
        foundFaculty.setName(faculty.getName());
        return facultyRepository.save(foundFaculty);
    }

    public void deleteFaculty(Long facultyId) {
        logger.info("Was invoked method for deleting a faculty by id: {}", facultyId);
        facultyRepository.deleteById(facultyId);
    }

    public Collection<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for searching a faculties by a color {}", color);
        return facultyRepository.findAll()
                .stream()
                .filter(e -> e.getColor().equals(color))
                .collect(Collectors.toList());
    }

    public Collection<Faculty> findFacultyByName(String name) {
        logger.info("Was invoked method for searching a faculty by nane {}", name);
        return facultyRepository.findFacultiesByNameContainsIgnoreCase(name);
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        logger.info("Was invoked method for searching a faculty by nane {}", color);
        return facultyRepository.findFacultiesByColorIgnoreCase(color);
    }

    public Set<Student> getFacultyStudents(Long id) {
        logger.info("Was invoked method for searching a faculty's students");
        return facultyRepository.findById(id).orElseThrow(FacultyNotFoundException::new).getStudents();
    }
}
