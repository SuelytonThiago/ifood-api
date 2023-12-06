package com.example.msstore.rest.controllers.integrationTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.config.ConstantsTest;
import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Categories;
import com.example.msstore.domain.repositories.CategoryRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rest.dto.CategoryDto;
import com.example.user.RoleResponse;
import com.example.user.RoleResponseList;
import com.example.user.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class CategoryControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static CategoryDto categoryDto;
    private static String token;
    private static Categories category;
    private static UserResponse adminResponse;
    private static RoleResponse adminRole;
    private static List<RoleResponse> adminRoles;
    private static RoleResponseList adminAuthorities;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @BeforeAll
    public static void beforeAll(){
        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/api/categories")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        token = JWT.create().withSubject("adm@example.com")
                .withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));

        adminRole = RoleResponse.newBuilder()
                .setId(1L)
                .setRoleName("ROLE_ADMIN")
                .build();
        adminRoles = new ArrayList<>();
        adminRoles.add(adminRole);
        adminAuthorities = RoleResponseList.newBuilder().addAllRoles(adminRoles).build();
        adminResponse = UserResponse.newBuilder()
                .setEmail("adm@example.com")
                .setId(1L)
                .setRoles(adminAuthorities).build();

    }

    @Test
    @Order(1)
    @DisplayName("integration test given categoryRequest object when createNewCategory then return HttpStatus isCreated")
    public void testCreateNewCategory(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);

        categoryDto = new CategoryDto("doces");
        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .header("Authorization","Bearer "+ token)
                .body(categoryDto)
                .when()
                .post("/create")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration test given categoryRequest object with empty categoryName when createNewCategory then throw CustomException")
    public void testCreateNewCategoryWithEmptyCategoryName(){
        CategoryDto cat = new CategoryDto("");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .header("Authorization","Bearer "+ token)
                .body(cat)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The categoryName cannot be empty or null"));

    }

    @Test
    @Order(3)
    @DisplayName("integration test given categoryRequest object with null categoryName when createNewCategory then throw CustomException")
    public void testCreateNewCategoryWithNullCategoryName(){
        CategoryDto cat = new CategoryDto();
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .header("Authorization","Bearer "+ token)
                .body(cat)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The categoryName cannot be empty or null"));
    }



    @Test
    @Order(4)
    @DisplayName("integration test when findAll then return CategoryResponse list")
    public void testFindAll() throws JsonProcessingException {
        var response = given().spec(specification)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString();
        List<Categories> list =objectMapper.readValue(response, List.class);
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @Order(5)
    @DisplayName("integration test given categoryId when delete then return HttpStatus isNoContent")
    public void testDelete(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        category = categoryRepository.findByName("doces").get();
        assertThat(category.getId()).isNotNull();
        given().spec(specification)
                .header("Authorization", "Bearer "+token)
                .when()
                .delete("/delete/{id}", category.getId())
                .then()
                .statusCode(204);

    }

    @Test
    @Order(6)
    @DisplayName("integration test given invalid categoryId when delete then throw ObjectNotFoundException")
    public void testDeleteWithInvalidCategoryId(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        var message = given().spec(specification)
                .header("Authorization", "Bearer "+token)
                .when()
                .delete("/delete/{id}",category.getId())
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(message).isEqualTo("The category is not found");
    }

}
