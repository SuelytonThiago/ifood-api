package com.example.msstore.domain.repositories;

import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class FoodRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private FoodRepository foodRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    private static Categories category;
    private static Food food;
    private static Store store;

    @Test
    @Order(1)
    @DisplayName("integration test given food object when save then return food object")
    public void testSave(){
        store = storeRepository.findByName("habbibs").get();
        category = categoryRepository.findByName("bebidas").get();
        var savedFood = foodRepository.save(new Food(null,"pizza",30.00,10,category,store));

        assertThat(savedFood.getId()).isNotNull();
        assertThat(savedFood.getName()).isEqualTo("pizza");
        assertThat(savedFood.getQuantityStock()).isEqualTo(10);
        assertThat(savedFood.getPrice()).isEqualTo(30.00);
        assertThat(savedFood.getFoodStore()).isEqualTo(store);
        assertThat(savedFood.getCategory()).isEqualTo(category);
        food = savedFood;
    }

    @Test
    @Order(2)
    @DisplayName("integration test given foodId when findById Then return foodObject")
    public void testFindById(){
        var outputFood = foodRepository.findById(food.getId());
        assertThat(outputFood).usingRecursiveComparison()
                .comparingOnlyFields("name", "price","quantityStock","category","foodStore")
                .isEqualTo(food);
    }

    @Test
    @Order(3)
    @DisplayName("integration test given category object when findByCategory then Return foodList")
    public void testFindByCategory(){
        var list = foodRepository.findByCategory(category);
        assertThat(list.size()).isEqualTo(2);
        assertThat(list).extracting("name", "price","quantityStock","category","foodStore")
                .contains(
                        tuple(food.getName(),food.getPrice(),food.getQuantityStock(),food.getCategory(),food.getFoodStore()));
    }

    @Test
    @Order(4)
    @DisplayName("integration test given food name when findByName then return foodObject ")
    public void testFindByName(){
        var outputFood = foodRepository.findByName(food.getName());
        assertThat(outputFood).usingRecursiveComparison()
                .comparingOnlyFields("name", "price","quantityStock","category","foodStore")
                .isEqualTo(food);
    }

    @Test
    @Order(4)
    @DisplayName("integration test when findAll then return foodList")
    public void testFindAll(){
        var list = foodRepository.findAll();
        assertThat(list.size()).isEqualTo(2);
        assertThat(list).extracting("name", "price","quantityStock","category","foodStore")
                .contains(
                        tuple(food.getName(),food.getPrice(),food.getQuantityStock(),food.getCategory(),food.getFoodStore()));
    }

    @Test
    @Order(5)
    @Transactional
    @DisplayName("integration test given food object when delete will delete food object from database")
    public void testDelete(){
        foodRepository.delete(food);
        assertThat(foodRepository.findById(food.getId()).isEmpty()).isTrue();
    }



}
