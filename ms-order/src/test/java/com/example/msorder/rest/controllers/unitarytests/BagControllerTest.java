package com.example.msorder.rest.controllers.unitarytests;

import com.example.msorder.domain.entities.Bag;
import com.example.msorder.rest.controllers.BagController;
import com.example.msorder.rest.dto.BagRequestDto;
import com.example.msorder.rest.dto.BagRequestUpdate;
import com.example.msorder.rest.dto.BagResponseDto;
import com.example.msorder.rest.services.BagService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class BagControllerTest {

    @Mock
    private BagService bagService;
    @InjectMocks
    private BagController bagController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private BagRequestDto bagRequestDto;
    private Bag bag;
    private BagRequestUpdate bagRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(bagController)
                .alwaysDo(print())
                .build();
        bagRequestDto = new BagRequestDto(
                1L,
                3
        );

        bag = new Bag(
                1L,
                1L,
                "habbibs",
                2,
                30.00,
                1L,
                Instant.now());

        bagRequestUpdate = new BagRequestUpdate(2);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("test given HttpServltRequest and BagRequest when create thn return HttpStatus 201")
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/bag/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bagRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user",  roles = "USER")
    @DisplayName("test when getBagItems then return BagResponseDto list")
    public void testGetBagItems() throws Exception {
        var list = Collections.singletonList(new BagResponseDto(bag));
        given(bagService.getBagItem(any(HttpServletRequest.class))).willReturn(list);

        mockMvc.perform(get("/api/bag"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test given bagId  when delete will delete bag from database")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/bag/delete/{id}",bag.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("test given bagId and bagRequestUpdate when update will update quantity bag item")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/bag/update/{id}",bag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bagRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
