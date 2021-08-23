package com.codecool.javaebremek.unittests;

import com.codecool.javaebremek.controller.AnimalController;
import com.codecool.javaebremek.model.Animal;
import com.codecool.javaebremek.service.AnimalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AnimalController.class})
public class AnimalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;

    private static final Animal TEST_ANIMAL1 = new Animal(null, "Jax", null, null);
    private static final Animal TEST_ANIMAL2 = new Animal(null, "Jun", null, null);
    private static final Animal TEST_ANIMAL_WITH_INVALID_NAME = new Animal(null, "", null, null);

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void add_inputGoodAnimal_shouldReturnAnimal() throws Exception {
        when(animalService.add(any())).thenReturn(TEST_ANIMAL1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_ANIMAL1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(TEST_ANIMAL1.getName())))
                .andExpect(jsonPath("$.owner", is(TEST_ANIMAL1.getOwner())))
                .andExpect(jsonPath("$.vet", is(TEST_ANIMAL1.getVet())));
        verify(animalService, times(1)).add(any());
    }

    @Test
    void updateById_inputGoodAnimal_shouldReturnAnimal() throws Exception {
        Long id = 1L;
        TEST_ANIMAL1.setId(id);
        when(animalService.updateById(anyLong(), any())).thenReturn(TEST_ANIMAL1);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/animals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_ANIMAL1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_ANIMAL1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_ANIMAL1.getName())))
                .andExpect(jsonPath("$.owner", is(TEST_ANIMAL1.getOwner())))
                .andExpect(jsonPath("$.vet", is(TEST_ANIMAL1.getVet())));
        verify(animalService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void updateById_inputInvalidAnimal_shouldReturnBadRequestStatus() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/animals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_ANIMAL_WITH_INVALID_NAME)))
                .andExpect(status().isBadRequest());

        verify(animalService, times(0)).updateById(anyLong(), any());
    }

    @Test
    void updateByNotExistingId_shouldReturnNotFoundStatus() throws Exception {
        Long id = 1L;
        when(animalService.updateById(anyLong(), any())).thenThrow(new RuntimeException());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/animals/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_ANIMAL1)))
                .andExpect(status().isNotFound());
        verify(animalService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void findById_inputValidId_shouldReturnGoodAnimal() throws Exception {
        Long id = 1L;
        TEST_ANIMAL1.setId(id);
        when(animalService.findById(anyLong())).thenReturn(java.util.Optional.of(TEST_ANIMAL1));
        mockMvc.perform(get("/animals/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_ANIMAL1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_ANIMAL1.getName())))
                .andExpect(jsonPath("$.owner", is(TEST_ANIMAL1.getOwner())))
                .andExpect(jsonPath("$.vet", is(TEST_ANIMAL1.getVet())));
        verify(animalService, times(1)).findById(id);
    }

    @Test
    void findAll_shouldReturnAllAnimals() throws Exception {
        TEST_ANIMAL1.setId(1L);
        TEST_ANIMAL2.setId(2L);
        List<Animal> animals = List.of(TEST_ANIMAL1, TEST_ANIMAL2);
        when(animalService.findAll()).thenReturn(animals);
        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(TEST_ANIMAL1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(TEST_ANIMAL1.getName())))
                .andExpect(jsonPath("$[0].owner", is(TEST_ANIMAL1.getOwner())))
                .andExpect(jsonPath("$[0].vet", is(TEST_ANIMAL1.getVet())))
                .andExpect(jsonPath("$[1].id", is(TEST_ANIMAL2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(TEST_ANIMAL2.getName())))
                .andExpect(jsonPath("$[1].owner", is(TEST_ANIMAL2.getOwner())))
                .andExpect(jsonPath("$[1].vet", is(TEST_ANIMAL2.getVet())));
        verify(animalService, times(1)).findAll();
    }

    @Test
    void deleteById_inputValidId_shouldReturnOkStatus() throws Exception {
        doNothing().when(animalService).deleteById(any());
        mockMvc.perform(delete("/animals/{id}", anyLong())).andExpect(status().isOk());
        verify(animalService, times(1)).deleteById(anyLong());
    }
}
