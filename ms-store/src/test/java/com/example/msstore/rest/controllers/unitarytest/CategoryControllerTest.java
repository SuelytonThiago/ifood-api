package com.example.msstore.rest.controllers.unitarytest;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.rest.controllers.CategoryController;
import com.example.msstore.rest.dto.CategoryDto;
import com.example.msstore.rest.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import static org.mockito.BDDMockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private CategoryDto categoryDto;
    private Categories category;

    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .alwaysDo(print()).build();
        category = new Categories(1L,"pizza");
        categoryDto = new CategoryDto("bebidas");
    }

    @Test
    @DisplayName("test when findAll then return categoryDto list")
    public void testFindAll() throws Exception {
        var list = Collections.singletonList(new CategoryDto(category));
        given(categoryService.findAll()).willReturn(list);

        mockMvc.perform(get("/api/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }


    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("test given categoryRequest object when createNewCategory then return HttpStatus isCreated")
    public void testCreateNewCategory() throws Exception {
        mockMvc.perform(post("/api/categories/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("test given categoryRequest object with null name when createNewCategory then return HttpStatus isBadRequest")
    public void testCreateNewCategoryWithNullName() throws Exception {
        categoryDto.setName(null);
        mockMvc.perform(post("/api/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("test given categoryRequest object with empty name when createNewCategory then return HttpStatus isBadRequest")
    public void testCreateNewCategoryWithEmptyName() throws Exception {
        categoryDto.setName("");
        mockMvc.perform(post("/api/categories/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    @DisplayName("test given categoryId when delete will delete category from database")
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/api/categories/delete/{id}",category.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
