package com.example.msstore.rest.controllers.unitarytest;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.rest.controllers.FoodController;
import com.example.msstore.rest.dto.FoodDto;
import com.example.msstore.rest.dto.FoodRequestUpdate;
import com.example.msstore.rest.services.FoodService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.BDDMockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Food food;
    private FoodDto foodDto;
    private Store store;
    private Categories category;
    private FoodRequestUpdate foodRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc= MockMvcBuilders.standaloneSetup(foodController)
                .alwaysDo(print()).build();

        category = new Categories(1L,"pizza");
        store = new Store(1L,"pizzaria","vendemos pizza",1L, TypeCode.RESTAURANT);
        food = new Food(1L,"pizza",30.00,10,category,store);
        foodDto = new FoodDto("pizza calabresa",30.00,10,"pizza");
        foodRequestUpdate = new FoodRequestUpdate("pizza calabresa", 30.00,10, category.getName());
    }

    @Test
    @DisplayName("test given categoryName when findByCategory then return foodDto list")
    public void testFindByCategory() throws Exception {
        var list = Collections.singletonList(new FoodDto(food));
        given(foodService.findByCategory(anyString())).willReturn(list);
        mockMvc.perform(get("/api/foods/category")
                .param("category",category.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @DisplayName("test given foodRequest object and storeId when create then return HttpStatus isCreated")
    @WithMockUser(username = "owner", roles = "OWNER")
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with null name and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithNullName() throws Exception {
        foodDto.setName(null);
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with null price and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithNullPrice() throws Exception {
        foodDto.setPrice(null);
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with null quantityStock and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithNullQuantityStock() throws Exception {
        foodDto.setQuantityStock(null);
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with null categoryName and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithNullCategoryName() throws Exception {
        foodDto.setCategoryName(null);
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with Empty Name and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithEmptyName() throws Exception {
        foodDto.setName("");
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodRequest object with empty categoryName and storeId when create then return HttpStatus isBadRequest")
    public void testCreateWithEmptyCategoryName() throws Exception {
        foodDto.setCategoryName("");
        mockMvc.perform(post("/api/foods/create/{idRestaurant}",store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodId when delete will delete food from database")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/foods/delete/{id}",food.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodId and foodRequest object when update will update food data")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/foods/update/{id}",food.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(foodRequestUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodId and foodRequest object with name contain only spaces when update then return HttpStatus isBadRRequest")
    public void testUpdateWithNameContainOnlySpaces() throws Exception {
        foodRequestUpdate.setName("   ");
        mockMvc.perform(patch("/api/foods/update/{id}",food.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "owner", roles = "OWNER")
    @DisplayName("test given foodId and foodRequest object with categoryName contain only spaces when update then return HttpStatus isBadRRequest")
    public void testUpdateWithCategoryNameContainOnlySpaces() throws Exception {
        foodRequestUpdate.setName("   ");
        mockMvc.perform(patch("/api/foods/update/{id}",food.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foodRequestUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }






}
