package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.repositories.CategoryRepository;
import com.example.msstore.rest.dto.CategoryDto;
import com.example.msstore.rest.services.exceptions.AlreadyExistException;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    public CategoryService categoryService;
    private Categories category;
    private CategoryDto categoryDto;

    @BeforeEach
    public void beforeEach(){
        category = new Categories(1L,"pizza");
        categoryDto = new CategoryDto("bebidas");
    }

    @Test
    @DisplayName("test Given CategoryDto Object when CreateNewCategory Will Save Category")
    public void testCreateNewCategory(){
        categoryService.createNewCategory(categoryDto);

        verify(categoryRepository).save(any(Categories.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given CategoryDto Object With Name Category Already In Use when CreateNewCategory then Return AlreadyExistException")
    public void testCreateNewCategoryWithNameCategoryAlreadyInUse(){
        categoryDto.setName(category.getName());
        given(categoryRepository.save(any(Categories.class))).willThrow(DataIntegrityViolationException.class);

        assertThatExceptionOfType(AlreadyExistException.class)
                .isThrownBy(() ->  categoryService.createNewCategory(categoryDto))
                .withMessage("This category already exist");
        verify(categoryRepository).save(any(Categories.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test when findAll then Return CategoryList")
    public void testFindAll(){
        given(categoryRepository.findAll()).willReturn(Collections.singletonList(category));

        var list = categoryService.findAll();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getName()).isEqualTo(category.getName());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given CategoryId When FindById Then Return CategoryObject")
    public void testFindById(){
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        Categories categoryOutput = categoryService.findById(category.getId());

        assertThat(categoryOutput.getName()).isEqualTo(category.getName());
        assertThat(categoryOutput.getId()).isEqualTo(category.getId());
        verify(categoryRepository).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given With Invalid CategoryId When FindById Then Throw ObjectNotFoundException")
    public void testFindByIdWithInvalidCategoryId(){
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> categoryService.findById(10L))
                .withMessage("The category is not found");
        verify(categoryRepository).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given CategoryName When FindByName Then Return CategoryObject")
    public void testFindByName(){
        given(categoryRepository.findByName(anyString())).willReturn(Optional.of(category));

        Categories categoryOutput = categoryService.findByName(category.getName());

        assertThat(categoryOutput.getName()).isEqualTo(category.getName());
        assertThat(categoryOutput.getId()).isEqualTo(category.getId());
        verify(categoryRepository).findByName(anyString());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given With Invalid CategoryName When FindById Then Throw ObjectNotFoundException")
    public void testFindByNameWithInvalidCategoryId(){
        given(categoryRepository.findByName(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> categoryService.findByName(category.getName()))
                .withMessage("The category is not found");
    }

    @Test
    @DisplayName("test Given CategoryId When DeleteById Will Delete Category")
    public void testDeleteById(){
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        categoryService.deleteById(category.getId());
        verify(categoryRepository).findById(anyLong());
        verify(categoryRepository).delete(any(Categories.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("test Given With Invalid CategoryId When DeleteById Then Throw ObjectNotFoundException")
    public void testDeleteByIdWithInvalidCategoryId(){
        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> categoryService.deleteById(10L))
                .withMessage("The category is not found");
        verify(categoryRepository).findById(anyLong());
        verifyNoMoreInteractions(categoryRepository);
    }
}
