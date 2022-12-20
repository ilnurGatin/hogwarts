package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import ru.hogwarts.school.model.Student;


import java.net.URI;
import java.util.Collection;
import java.util.Set;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HogwartsApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createStudentTest() {
        Student student = givenStudent("StudentName", 25);
        ResponseEntity<Student> response = whenCreatingStudentRequest(getUriBuilder().build().toUri(), student);
        thenStudentCreated(response);
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port).path("/hogwarts/student");
    }

    @Test
    public void getStudentByIdTest() {
        Student student = givenStudent("StudentName", 25);
        ResponseEntity<Student> createResponse = whenCreatingStudentRequest(getUriBuilder().build().toUri(), student);
        thenStudentCreated(createResponse);
        Student createdStudent = createResponse.getBody();
        thenStudentIsFound(createdStudent.getId(), createdStudent);
    }

    private void thenStudentIsFound(Long studentId, Student student) {
        URI uri = getUriBuilder().path("/{id}").buildAndExpand(studentId).toUri();
        ResponseEntity<Student> response = restTemplate.getForEntity(uri, Student.class);
        Assertions.assertThat(response.getBody()).isEqualTo(student);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void findByAgeBetweenTest() {
        Student student1 = givenStudent("StudentName1", 21);
        Student student2 = givenStudent("StudentName2", 22);
        Student student3 = givenStudent("StudentName3", 23);
        Student student4 = givenStudent("StudentName4", 24);

        whenCreatingStudentRequest(getUriBuilder().build().toUri(), student1);
        whenCreatingStudentRequest(getUriBuilder().build().toUri(), student2);
        whenCreatingStudentRequest(getUriBuilder().build().toUri(), student3);
        whenCreatingStudentRequest(getUriBuilder().build().toUri(), student4);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("age", "22");
        queryParams.add("age2", "23");
        thenStudentsAreFoundByCriteria(queryParams, student2, student3);

    }

    @Test
    public void updateTest() {
        Student student = givenStudent("StudentName", 25);

        ResponseEntity<Student> responseEntity = whenCreatingStudentRequest(getUriBuilder().build().toUri(), student);
        thenStudentCreated(responseEntity);
        Student createdStudent = responseEntity.getBody();

        whenUpdatingStudent(createdStudent, 32, "newName");
        thenUpdateStudent(createdStudent, 32, "newName");
    }

    private void thenUpdateStudent(Student createdStudent, int newAge, String newName) {
        URI getUri = getUriBuilder().cloneBuilder().path("/{id}").buildAndExpand(createdStudent.getId()).toUri();
        ResponseEntity<Student> updatedStudentRs = restTemplate.getForEntity(getUri, Student.class);

        Assertions.assertThat(updatedStudentRs.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(updatedStudentRs.getBody()).isNotNull();
        Assertions.assertThat(updatedStudentRs.getBody().getAge()).isEqualTo(newAge);
        Assertions.assertThat(updatedStudentRs.getBody().getName()).isEqualTo(newName);
    }

    private void whenUpdatingStudent(Student createdStudent, int newAge, String newName) {
        createdStudent.setAge(newAge);
        createdStudent.setName(newName);

        restTemplate.put(getUriBuilder().build().toUri(), createdStudent);
    }

    @Test
    public void deleteTest() {
        Student student = givenStudent("StudentName", 25);

        ResponseEntity<Student> responseEntity = whenCreatingStudentRequest(getUriBuilder().build().toUri(), student);
        thenStudentCreated(responseEntity);
        Student createdStudent = responseEntity.getBody();

        whenDeletingStudent(createdStudent);
        thenStudentNotFound(createdStudent);


    }

    private void whenDeletingStudent(Student createdStudent) {
        restTemplate.delete(getUriBuilder().path("/{id}").buildAndExpand(createdStudent.getId()).toUri());
    }

    private Student givenStudent(String name, int age) {
        return new Student(name, age);
    }

    private ResponseEntity<Student> whenCreatingStudentRequest(URI uri, Student student) {
        return restTemplate.postForEntity(uri, student, Student.class);
    }

    private void thenStudentCreated(ResponseEntity<Student> response) {
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getId()).isNotNull();
    }

    private void restId(Collection<Student> students) {
        students.forEach(it -> it.setId(null));
    }

    private void thenStudentsAreFoundByCriteria(MultiValueMap<String, String> queryParams, Student... students) {
        URI uri = getUriBuilder().queryParams(queryParams).build().toUri();
        ResponseEntity<Set<Student>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Set<Student>>() {
        });

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Set<Student> actualResult = response.getBody();
        restId(actualResult);
        Assertions.assertThat(actualResult).containsExactlyInAnyOrder(students);
    }

    private void thenStudentNotFound(Student student) {
        URI getUri = getUriBuilder().path("/{id}").buildAndExpand(student.getId()).toUri();
        ResponseEntity<Student> emptyRs = restTemplate.getForEntity(getUri, Student.class);

        Assertions.assertThat(emptyRs.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
