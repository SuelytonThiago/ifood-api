package com.example.msstore.rest.services;

import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.domain.repositories.FoodRepository;
import com.example.msstore.rest.dto.FoodDto;
import com.example.msstore.rest.dto.FoodRequestUpdate;
import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private StoreService  storeService;

    @InjectMocks
    private FoodService foodService;
    private Food food;
    private Categories category;
    private Store store;

    private FoodDto foodDto;
    private FoodRequestUpdate foodRequestUpdate;

    @BeforeEach
    public void beforeEach(){
        category = new Categories(1L,"pizza");
        store = new Store(1L,"pizzaria","vendemos pizza",1L,TypeCode.RESTAURANT);
        food = new Food(1L,"pizza",30.00,10,category,store);
        foodDto = new FoodDto("pizza calabresa",30.00,10,"pizza");
        foodRequestUpdate = new FoodRequestUpdate("pizza queijo",30.00,10,"pizza");
    }

    @Test
    @DisplayName("test given foodDto object and idStore when createNewFood will save food object")
    public void testCreateNewFood(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(categoryService.findByName(anyString())).willReturn(category);
        given(storeService.findById(anyLong())).willReturn(store);
        given(storeService.verifyOwnerStore(anyLong(),any(HttpServletRequest.class))).willReturn(true);

        foodService.createNewFood(foodDto,store.getId(),request);

        verify(categoryService).findByName(anyString());
        verify(storeService).verifyOwnerStore(anyLong(),any(HttpServletRequest.class));
        verify(storeService).findById(anyLong());
        verify(foodRepository).save(any(Food.class));
        verifyNoMoreInteractions(categoryService);
        verifyNoMoreInteractions(storeService);
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given FoodId When FindById Then Return Food Object")
    public void testFindById(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));

        var foodOutput = foodService.findById(food.getId());

        assertThat(foodOutput).usingRecursiveComparison().isEqualTo(food);
        verify(foodRepository).findById(anyLong());
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given With Invalid FoodId When FindById Then Throw ObjectNotFoundException")
    public void testFindByIdWithInvalidFoodId(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(()-> foodService.findById(10L))
                .withMessage("The food cannot found");
        verify(foodRepository).findById(anyLong());
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given categoryName When FindByCategory Then Return foodDtoList")
    public void testFindByCategory(){
        given(categoryService.findByName(anyString())).willReturn(category);
        given(foodRepository.findByCategory(any(Categories.class))).willReturn(Collections.singletonList(food));

        var list = foodService.findByCategory(category.getName());
        assertThat(list).extracting("name","price","quantityStock","categoryName")
                .contains(
                        tuple(food.getName(),
                                food.getPrice(),
                                food.getQuantityStock(),
                                food.getCategory().getName()));
        verify(categoryService).findByName(anyString());
        verify(foodRepository).findByCategory(any(Categories.class));
        verifyNoMoreInteractions(categoryService);
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given foodId When DeleteById Will Delete Food Object")
    public void testDeleteById(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));
        given(storeService.findById(anyLong())).willReturn(store);
        foodService.deleteById(food.getId());

        verify(storeService).findById(anyLong());
        verify(foodRepository).findById(anyLong());
        verify(foodRepository).delete(any(Food.class));
        verify(storeService).save(any(Store.class));
        verifyNoMoreInteractions(foodRepository);
        verifyNoMoreInteractions(storeService);
    }

    @Test
    @DisplayName("test Given With Invalid FoodId When DeleteById Then Throw ObjectNotFoundException")
    public void testDeleteByIdWithInvalidFoodId(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> foodService.deleteById(10L))
                        .withMessage("The food cannot found");
        verify(foodRepository).findById(anyLong());
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given FoodId And FoodRequest Object When UpdateFoodData Will Update Food Data")
    public void testUpdateFoodData(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.of(food));

        foodService.updateFoodData(food.getId(), foodRequestUpdate);
        verify(foodRepository).findById(anyLong());
        verify(foodRepository).save(any(Food.class));
        verifyNoMoreInteractions(foodRepository);
    }

    @Test
    @DisplayName("test Given With Invalid FoodId And FoodRequest Object When UpdateFoodData Then Throw ObjectNotFoundException")
    public void testUpdateFoodDataWithInvalidFoodId(){
        given(foodRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> foodService.updateFoodData(10L, foodRequestUpdate))
                .withMessage("The food cannot found");
        verify(foodRepository).findById(anyLong());
        verifyNoMoreInteractions(foodRepository);
    }


}
