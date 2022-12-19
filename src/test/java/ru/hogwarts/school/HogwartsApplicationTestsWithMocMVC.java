package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FacultyController.class)
public class HogwartsApplicationTestsWithMocMVC {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Faculty1";
        String color = "white";

        JSONObject facultyObj = new JSONObject();
        facultyObj.put("id", id);
        facultyObj.put("name", name);
        facultyObj.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void findFacultyByColorTest() throws Exception {
        Long id1 = 1L;
        String name1 = "Faculty1";

        Long id2 = 2L;
        String name2 = "Faculty2";

        String color = "white";

        Faculty faculty1 = new Faculty();
        faculty1.setColor(color);
        faculty1.setName(name1);
        faculty1.setId(id1);

        Faculty faculty2 = new Faculty();
        faculty2.setId(id2);
        faculty2.setName(name2);
        faculty2.setColor(color);

        when(facultyRepository.findFacultiesByColorIgnoreCase(color)).thenReturn(Set.of(faculty1, faculty2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .queryParam("color", color)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(faculty1, faculty2))));
    }

    @Test
    public void addFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Faculty";
        String color = "White";

        JSONObject facultyObj = new JSONObject();
        facultyObj.put("name", name);
        facultyObj.put("color", color);

        Faculty faculty = new Faculty();
        faculty.setColor(color);
        faculty.setName(name);
        faculty.setId(id);

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void updateFacultyTest() throws Exception {
        Long id = 1L;
        String oldName = "Faculty";
        String oldColor = "White";

        String newName = "Faculty2";
        String newColor = "Black";

        JSONObject facultyObj = new JSONObject();
        facultyObj.put("id", id)
        facultyObj.put("name", newName);
        facultyObj.put("color", newColor);

        Faculty faculty = new Faculty();
        faculty.setColor(oldColor);
        faculty.setName(oldName);
        faculty.setId(id);

        Faculty updatedFaculty = new Faculty();
        faculty.setColor(newColor);
        faculty.setName(newName);
        faculty.setId(id);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObj.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.color").value(newColor));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Long id = 1L;
        String name = "Faculty";
        String color = "White";

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/faculty/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));

        verify(facultyRepository, atLeastOnce()).deleteById(id);
    }
}
