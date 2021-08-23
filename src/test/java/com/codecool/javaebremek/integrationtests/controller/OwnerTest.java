package com.codecool.javaebremek.integrationtests.controller;

import com.codecool.javaebremek.model.Owner;
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
public class OwnerTest {

    @LocalServerPort
    private int port;
    private String baseUrl;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setBaseUrl() {
        this.baseUrl = "http://localhost:" + port + "/owners";
    }

    @Test
    public void addNewOwner_emptyDatabase_shouldReturnSameOwner() {
        Owner testOwner = new Owner(null, "Jax", null);
        Owner result = testRestTemplate.postForObject(baseUrl, testOwner, Owner.class);
        assertEquals(testOwner.getName(), result.getName());
    }

    @Test
    public void getOwners_emptyDatabase_returnsEmptyList() {
        List<Owner> owners = List.of(testRestTemplate.getForObject(baseUrl, Owner[].class));
        assertEquals(0, owners.size());
    }

    @Test
    public void getOwnerById_withOnePostedOwner_returnsOwnerWithSameId() {
        Owner testOwner = new Owner(null, "Joe", null);
        Owner testOwnerResult = testRestTemplate.postForObject(baseUrl, testOwner, Owner.class);
        Owner result = testRestTemplate.getForObject(baseUrl + "/" + testOwnerResult.getId(), Owner.class);
        assertEquals(testOwnerResult.getId(), result.getId());
    }

    @Test
    public void updateOwner_withoutOwner_returnsBadRequest() {
        Owner testOwner = new Owner(null, "Joy", null);
        HttpEntity<Owner> httpEntity = createHttpEntityWithMediaTypeJson(testOwner);
        ResponseEntity<Object> putResponse = testRestTemplate.exchange(baseUrl + "55", HttpMethod.PUT, httpEntity, Object.class);
        assertEquals(HttpStatus.NOT_FOUND, putResponse.getStatusCode());
    }

    @Test
    public void updateOwner_withOnePostedOwner_returnsUpdatedOwner() {
        Owner testOwner = new Owner(null, "Jan", null);
        Owner testOwnerResult = testRestTemplate.postForObject(baseUrl, testOwner, Owner.class);
        testOwner.setName("Updated Jan");
        testRestTemplate.put(baseUrl + "/" + testOwnerResult.getId(), testOwner);
        Owner updatedOwner = testRestTemplate.getForObject(baseUrl + "/" + testOwnerResult.getId(), Owner.class);
        assertEquals("Updated Jan", updatedOwner.getName());

    }

    @Test
    public void deleteOwnerById_withSomePostedOwners_getAllShouldReturnRemainingOwners() {
        Owner testOwner1 = new Owner(null, "Jay", null);
        Owner testOwner2 = new Owner(null, "Jim", null);
        Owner testOwner3 = new Owner(null, "Jun", null);
        List<Owner> testOwners = new ArrayList<>();
        testOwners.add(testOwner1);
        testOwners.add(testOwner2);
        testOwners.add(testOwner3);

        testOwners.forEach(testOwner -> testOwner.setId(testRestTemplate.postForObject(baseUrl, testOwner, Owner.class).getId()));
        testRestTemplate.delete(baseUrl + "/" + testOwner2.getId());
        testOwners.remove(testOwner2);
        List<Owner> remainingOwners = List.of(testRestTemplate.getForObject(baseUrl, Owner[].class));
        assertEquals(testOwners.size(), remainingOwners.size());
        assertTrue(contain(remainingOwners, testOwner1));
        assertTrue(contain(remainingOwners, testOwner3));
    }

    private HttpEntity<Owner> createHttpEntityWithMediaTypeJson(Owner testOwner) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(testOwner, headers);
    }

    private boolean equalsWithId(Owner owner, Object object) {
        if (owner == object) return true;
        if (owner == null || object == null || owner.getClass() != object.getClass()) return false;

        Owner otherOwner = (Owner) object;

        if (!Objects.equals(owner.getId(), otherOwner.getId())) return false;
        if (!Objects.equals(owner.getName(), otherOwner.getName())) return false;
        return true;
    }

    private boolean contain(List<Owner> owners, Owner owner) {
        for (Owner otherOwner : owners) {
            if (equalsWithId(owner, otherOwner)) return true;
        }
        return false;
    }
}
