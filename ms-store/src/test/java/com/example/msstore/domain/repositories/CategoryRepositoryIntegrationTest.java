package com.example.msstore.domain.repositories;

import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Categories;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class CategoryRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    private static Categories category;

    @Test
    @Order(1)
    @DisplayName("integration test given categoryObject when save then return category object")
    public void testSave(){
        var savedCategory = categoryRepository.save(new Categories(null,"pizza"));

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("pizza");

        category = savedCategory;
    }

    @Test
    @Order(2)
    @DisplayName("integration test given categoryId when findById then return category Object")
    public void testFindById(){
        var outputCategory = categoryRepository.findById(category.getId()).get();

        assertThat(outputCategory.getName()).isEqualTo(category.getName());
    }

    @Test
    @Order(3)
    @DisplayName("integration test given categoryName when findByName then return category Object ")
    public void testFindByName(){
        var outputCategory = categoryRepository.findByName(category.getName()).get();

        assertThat(outputCategory.getName()).isEqualTo(category.getName());
    }

    @Test
    @Order(4)
    @DisplayName("integration tet when findAll then return categoryList")
    public void testFindAll(){
        var list = categoryRepository.findAll();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getName()).isEqualTo("bebidas");
        assertThat(list.get(1).getName()).isEqualTo("pizza");
    }

    @Test
    @Order(5)
    @DisplayName("integration test given categoryObject when delete will delete category from database")
    public void testDelete(){
        categoryRepository.delete(category);
        assertThat(categoryRepository.findById(category.getId()).isEmpty()).isTrue();
    }
}
