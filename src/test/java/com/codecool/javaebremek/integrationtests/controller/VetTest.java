package com.codecool.javaebremek.integrationtests.controller;

import com.codecool.javaebremek.model.Vet;
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
public class VetTest {

    @LocalServerPort
    private int port;
    private String baseUrl;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setBaseUrl() {
        this.baseUrl = "http://localhost:" + port + "/vets";
    }

    @Test
    public void addNewVet_emptyDatabase_shouldReturnSameVet() {
        Vet testVet = new Vet(null, "Jax", null);
        Vet result = testRestTemplate.postForObject(baseUrl, testVet, Vet.class);
        assertEquals(testVet.getName(), result.getName());
    }

    @Test
    public void getVets_emptyDatabase_returnsEmptyList() {
        List<Vet> vets = List.of(testRestTemplate.getForObject(baseUrl, Vet[].class));
        assertEquals(0, vets.size());
    }

    @Test
    public void getVetById_withOnePostedVet_returnsVetWithSameId() {
        Vet testVet = new Vet(null, "Joe", null);
        Vet testVetResult = testRestTemplate.postForObject(baseUrl, testVet, Vet.class);
        Vet result = testRestTemplate.getForObject(baseUrl + "/" + testVetResult.getId(), Vet.class);
        assertEquals(testVetResult.getId(), result.getId());
    }

    @Test
    public void updateVet_withoutVet_returnsBadRequest() {
        Vet testVet = new Vet(null, "Joy", null);
        HttpEntity<Vet> httpEntity = createHttpEntityWithMediaTypeJson(testVet);
        ResponseEntity<Object> putResponse = testRestTemplate.exchange(baseUrl + "55", HttpMethod.PUT, httpEntity, Object.class);
        assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());
    }

    @Test
    public void updateVet_withOnePostedVet_returnsUpdatedVet() {
        Vet testVet = new Vet(null, "Jan", null);
        Vet testVetResult = testRestTemplate.postForObject(baseUrl, testVet, Vet.class);
        testVet.setName("Updated Jan");
        testRestTemplate.put(baseUrl + "/" + testVetResult.getId(), testVet);
        Vet updatedVet = testRestTemplate.getForObject(baseUrl + "/" + testVetResult.getId(), Vet.class);
        assertEquals("Updated Jan", updatedVet.getName());

    }

    @Test
    public void deleteVetById_withSomePostedVets_getAllShouldReturnRemainingVets() {
        Vet testVet1 = new Vet(null, "Jay", null);
        Vet testVet2 = new Vet(null, "Jim", null);
        Vet testVet3 = new Vet(null, "Jun", null);
        List<Vet> testVets = new ArrayList<>();
        testVets.add(testVet1);
        testVets.add(testVet2);
        testVets.add(testVet3);

        testVets.forEach(testVet -> testVet.setId(testRestTemplate.postForObject(baseUrl, testVet, Vet.class).getId()));
        testRestTemplate.delete(baseUrl + "/" + testVet2.getId());
        testVets.remove(testVet2);
        List<Vet> remainingVets = List.of(testRestTemplate.getForObject(baseUrl, Vet[].class));
        assertEquals(testVets.size(), remainingVets.size());
        assertTrue(contain(remainingVets, testVet1));
        assertTrue(contain(remainingVets, testVet3));
    }

    private HttpEntity<Vet> createHttpEntityWithMediaTypeJson(Vet testVet) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(testVet, headers);
    }

    private boolean equalsWithId(Vet vet, Object object) {
        if (vet == object) return true;
        if (vet == null || object == null || vet.getClass() != object.getClass()) return false;

        Vet otherVet = (Vet) object;

        if (!Objects.equals(vet.getId(), otherVet.getId())) return false;
        if (!Objects.equals(vet.getName(), otherVet.getName())) return false;
        return true;
    }

    private boolean contain(List<Vet> vets, Vet vet) {
        for (Vet otherVet : vets) {
            if (equalsWithId(vet, otherVet)) return true;
        }
        return false;
    }
}
