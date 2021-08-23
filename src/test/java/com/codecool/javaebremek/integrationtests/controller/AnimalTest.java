package com.codecool.javaebremek.integrationtests.controller;

import com.codecool.javaebremek.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AnimalTest {

    @LocalServerPort
    private int port;
    private String baseUrl;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setBaseUrl() {
        this.baseUrl = "http://localhost:" + port + "/animals";
    }

    @Test
    public void addNewAnimal_emptyDatabase_shouldReturnSameAnimal() {
        Animal testAnimal = new Animal(null, "Jax", null, null);
        Animal result = testRestTemplate.postForObject(baseUrl, testAnimal, Animal.class);
        assertEquals(testAnimal.getName(), result.getName());
    }

    @Test
    public void getAnimals_emptyDatabase_returnsEmptyList() {
        List<Animal> animals = List.of(testRestTemplate.getForObject(baseUrl, Animal[].class));
        assertEquals(0, animals.size());
    }

    @Test
    public void getAnimalById_withOnePostedAnimal_returnsAnimalWithSameId() {
        Animal testAnimal = new Animal(null, "Joe", null, null);
        Animal testAnimalResult = testRestTemplate.postForObject(baseUrl, testAnimal, Animal.class);
        Animal result = testRestTemplate.getForObject(baseUrl + "/" + testAnimalResult.getId(), Animal.class);
        assertEquals(testAnimalResult.getId(), result.getId());
    }

    @Test
    public void updateAnimal_withoutAnimal_returnsBadRequest() {
        Animal testAnimal = new Animal(null, "Joy", null, null);
        HttpEntity<Animal> httpEntity = createHttpEntityWithMediaTypeJson(testAnimal);
        ResponseEntity<Object> putResponse = testRestTemplate.exchange(baseUrl + "55", HttpMethod.PUT, httpEntity, Object.class);
        assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());
    }

    @Test
    public void updateAnimal_withOnePostedAnimal_returnsUpdatedAnimal() {
        Animal testAnimal = new Animal(null, "Jan", null, null);
        Animal testAnimalResult = testRestTemplate.postForObject(baseUrl, testAnimal, Animal.class);
        testAnimal.setName("Updated Jan");
        testRestTemplate.put(baseUrl + "/" + testAnimalResult.getId(), testAnimal);
        Animal updatedAnimal = testRestTemplate.getForObject(baseUrl + "/" + testAnimalResult.getId(), Animal.class);
        assertEquals("Updated Jan", updatedAnimal.getName());

    }

    @Test
    public void deleteAnimalById_withSomePostedAnimals_getAllShouldReturnRemainingAnimals() {
        Animal testAnimal1 = new Animal(null, "Jay", null, null);
        Animal testAnimal2 = new Animal(null, "Jim", null, null);
        Animal testAnimal3 = new Animal(null, "Jun", null, null);
        List<Animal> testAnimals = new ArrayList<>();
        testAnimals.add(testAnimal1);
        testAnimals.add(testAnimal2);
        testAnimals.add(testAnimal3);

        testAnimals.forEach(testAnimal -> testAnimal.setId(testRestTemplate.postForObject(baseUrl, testAnimal, Animal.class).getId()));
        testRestTemplate.delete(baseUrl + "/" + testAnimal2.getId());
        testAnimals.remove(testAnimal2);
        List<Animal> remainingAnimals = List.of(testRestTemplate.getForObject(baseUrl, Animal[].class));
        assertEquals(testAnimals.size(), remainingAnimals.size());
        assertTrue(contain(remainingAnimals, testAnimal1));
        assertTrue(contain(remainingAnimals, testAnimal3));
    }

    private HttpEntity<Animal> createHttpEntityWithMediaTypeJson(Animal testAnimal) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(testAnimal, headers);
    }

    private boolean equalsWithId(Animal animal, Object object) {
        if (animal == object) return true;
        if (animal == null || object == null || animal.getClass() != object.getClass()) return false;

        Animal otherAnimal = (Animal) object;

        if (!Objects.equals(animal.getId(), otherAnimal.getId())) return false;
        if (!Objects.equals(animal.getName(), otherAnimal.getName())) return false;
        return true;
    }

    private boolean contain(List<Animal> animals, Animal animal) {
        for (Animal otherAnimal : animals) {
            if (equalsWithId(animal, otherAnimal)) return true;
        }
        return false;
    }
}
