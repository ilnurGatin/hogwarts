package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.*;
import java.util.stream.Stream;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public ResponseEntity<Faculty> addFaculty(@RequestBody Faculty faculty) {
        return ResponseEntity.ok(facultyService.addFaculty(faculty));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        Faculty foundFaculty = facultyService.findFaculty(id);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @GetMapping("/{facultyId}/Students")
    public ResponseEntity<Set<Student>> getFacultyStudents(@PathVariable("facultyId") Long id) {
        return ResponseEntity.ok(facultyService.getFacultyStudents(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<Faculty>> findFaculties(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String color) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.findFacultyByName(name));
        }
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findFacultiesByColor(color));
        }
        return ResponseEntity.ok(facultyService.getAll());
    }

    @GetMapping("all/longestName")
    public Optional<Map.Entry<Integer, List<String>>> findLongestName() {
        return facultyService.findLongestFacultyName();
    }

    @GetMapping("/sum")
    public int getSum() {
        return Stream.iterate(1, a -> a + 1).limit(1_000_000).parallel().reduce(0, Integer::sum);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable("id") long id, @RequestBody Faculty faculty) {
        Faculty updatedFaculty = facultyService.editFaculty(id, faculty);
        return ResponseEntity.ok(updatedFaculty);
    }

    @DeleteMapping("{facultyId}")
    public ResponseEntity<Faculty> deleteFaculty(@PathVariable Long facultyId) {
        facultyService.deleteFaculty(facultyId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Faculty>> getFacultiesByColor(@RequestParam("color") String color) {
        if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findFacultiesByColor(color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }
}
