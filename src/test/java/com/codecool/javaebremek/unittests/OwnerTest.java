package com.codecool.javaebremek.unittests;

import com.codecool.javaebremek.controller.OwnerController;
import com.codecool.javaebremek.model.Owner;
import com.codecool.javaebremek.service.OwnerService;
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

@WebMvcTest({OwnerController.class})
public class OwnerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    private static final Owner TEST_OWNER1 = new Owner(null, "Jax", null);
    private static final Owner TEST_OWNER2 = new Owner(null, "Jun", null);
    private static final Owner TEST_OWNER_WITH_INVALID_NAME = new Owner(null, "", null);

    public static String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void add_inputGoodOwner_shouldReturnOwner() throws Exception {
        when(ownerService.add(any())).thenReturn(TEST_OWNER1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_OWNER1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(TEST_OWNER1.getName())));
        verify(ownerService, times(1)).add(any());
    }

    @Test
    void updateById_inputGoodOwner_shouldReturnOwner() throws Exception {
        Long id = 1L;
        TEST_OWNER1.setId(id);
        when(ownerService.updateById(anyLong(), any())).thenReturn(TEST_OWNER1);
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_OWNER1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_OWNER1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_OWNER1.getName())));
        verify(ownerService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void updateById_inputInvalidOwner_shouldReturnBadRequestStatus() throws Exception {
        Long id = 1L;
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_OWNER_WITH_INVALID_NAME)))
                .andExpect(status().isBadRequest());

        verify(ownerService, times(0)).updateById(anyLong(), any());
    }

    @Test
    void updateByNotExistingId_shouldReturnNotFoundStatus() throws Exception {
        Long id = 1L;
        when(ownerService.updateById(anyLong(), any())).thenThrow(new RuntimeException());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/owners/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(asJsonString(TEST_OWNER1)))
                .andExpect(status().isNotFound());
        verify(ownerService, times(1)).updateById(anyLong(), any());
    }

    @Test
    void findById_inputValidId_shouldReturnGoodOwner() throws Exception {
        Long id = 1L;
        TEST_OWNER1.setId(id);
        when(ownerService.findById(anyLong())).thenReturn(java.util.Optional.of(TEST_OWNER1));
        mockMvc.perform(get("/owners/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(TEST_OWNER1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(TEST_OWNER1.getName())));
        verify(ownerService, times(1)).findById(id);
    }

    @Test
    void findAll_shouldReturnAllOwners() throws Exception {
        TEST_OWNER1.setId(1L);
        TEST_OWNER2.setId(2L);
        List<Owner> owners = List.of(TEST_OWNER1, TEST_OWNER2);
        when(ownerService.findAll()).thenReturn(owners);
        mockMvc.perform(get("/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(TEST_OWNER1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(TEST_OWNER1.getName())))
                .andExpect(jsonPath("$[1].id", is(TEST_OWNER2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(TEST_OWNER2.getName())));
        verify(ownerService, times(1)).findAll();
    }

    @Test
    void deleteById_inputValidId_shouldReturnOkStatus() throws Exception {
        doNothing().when(ownerService).deleteById(any());
        mockMvc.perform(delete("/owners/{id}", anyLong())).andExpect(status().isOk());
        verify(ownerService, times(1)).deleteById(anyLong());
    }
}
