package com.codecool.javaebremek.unittests;

import com.codecool.javaebremek.controller.VetController;
import com.codecool.javaebremek.model.Vet;
import com.codecool.javaebremek.service.VetService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({VetController.class})
public class VetTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    private static final Vet TEST_VET1 = new Vet(null, "Jax", null);
    private static final Vet TEST_VET2 = new Vet(null, "Jun", null);
    private static final Vet TEST_VET_WITH_INVALID_NAME = new Vet(null, "", null);

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void add_inputGoodVet_shouldReturnVet() throws Exception {
        when(vetService.add(any())).thenReturn(TEST_VET1);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(TEST_VET1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(TEST_VET1.getName())));
        verify(vetService, times(1)).add(any());
    }

    @Test
    void updateById_inputGoodVet_shouldReturnVet() throws Exception {
        Long id = 1L;
        TEST_VET1.setId(id);
        when(vetService.updateById(anyLong(), any())).thenReturn(TEST_VET1);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/vets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(TEST_VET1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_VET1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_VET1.getName())));
        verify(vetService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void updateById_inputInvalidVet_shouldReturnBadRequestStatus() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                .put("/vets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(TEST_VET_WITH_INVALID_NAME)))
                .andExpect(status().isBadRequest());

        verify(vetService, times(0)).updateById(anyLong(), any());
    }

    @Test
    void updateByNotExistingId_shouldReturnNotFoundStatus() throws Exception {
        Long id = 1L;
        when(vetService.updateById(anyLong(), any())).thenThrow(new RuntimeException());
        mockMvc.perform(MockMvcRequestBuilders
                .put("/vets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(TEST_VET1)))
                .andExpect(status().isNotFound());
        verify(vetService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void findById_inputValidId_shouldReturnGoodVet() throws Exception {
        Long id = 1L;
        TEST_VET1.setId(id);
        when(vetService.findById(anyLong())).thenReturn(java.util.Optional.of(TEST_VET1));
        mockMvc.perform(get("/vets/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_VET1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_VET1.getName())));
        verify(vetService, times(1)).findById(id);
    }

    @Test
    void findAll_shouldReturnAllVets() throws Exception {
        TEST_VET1.setId(1L);
        TEST_VET2.setId(2L);
        List<Vet> vets = List.of(TEST_VET1, TEST_VET2);
        when(vetService.findAll()).thenReturn(vets);
        mockMvc.perform(get("/vets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(TEST_VET1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(TEST_VET1.getName())))
                .andExpect(jsonPath("$[1].id", is(TEST_VET2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(TEST_VET2.getName())));
        verify(vetService, times(1)).findAll();
    }

    @Test
    void deleteById_inputValidId_shouldReturnOkStatus() throws Exception {
        doNothing().when(vetService).deleteById(any());
        mockMvc.perform(delete("/vets/{id}", anyLong())).andExpect(status().isOk());
        verify(vetService, times(1)).deleteById(anyLong());
    }
}
