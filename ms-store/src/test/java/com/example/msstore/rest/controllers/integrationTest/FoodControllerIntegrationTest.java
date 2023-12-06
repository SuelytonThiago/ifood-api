package com.example.msstore.rest.controllers.integrationTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.config.ConstantsTest;
import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Food;
import com.example.msstore.domain.repositories.FoodRepository;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rest.dto.FoodDto;
import com.example.msstore.rest.dto.FoodRequestUpdate;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class FoodControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static String token;
    private static Food food;
    private static UserResponse ownerResponse;
    private static RoleResponse ownerRole;
    private static List<RoleResponse> ownerRoles;
    private static RoleResponseList ownerAuthorities;


    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;


    @BeforeAll
    public static void beforeAll(){
        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/api/foods")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        token = JWT.create().withSubject("owner@example.com")
                .withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis()+600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));

        ownerRole = RoleResponse.newBuilder()
                .setId(2L)
                .setRoleName("ROLE_OWNER")
                .build();
        ownerRoles = new ArrayList<>();
        ownerRoles.add(ownerRole);
        ownerAuthorities = RoleResponseList.newBuilder().addAllRoles(ownerRoles).build();
        ownerResponse = UserResponse.newBuilder()
                .setEmail("owner@example.com")
                .setId(1L)
                .setRoles(ownerAuthorities).build();
    }

    @Test
    @Order(1)
    @DisplayName("integration test given foodRequest object when create then return HttpStatus isCreated")
    public void testCreate(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("whisky",30.0,10,"bebidas");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration test given foodRequest object with Empty food name when create then throw CustomException")
    public void testCreateWithEmptyFoodName(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("",30.0,10,"bebidas");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the food name cannot be empty or null"));
    }

    @Test
    @Order(3)
    @DisplayName("integration test given foodRequest object with null food name when create then throw CustomException")
    public void testCreateWithNullFoodName(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto(null,30.0,10,"bebidas");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the food name cannot be empty or null"));
    }

    @Test
    @Order(4)
    @DisplayName("integration test given foodRequest object with null food price when create then throw CustomException")
    public void testCreateWithNullFoodPrice(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("whisky",null,10,"bebidas");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the price cannot be null"));
    }

    @Test
    @Order(5)
    @DisplayName("integration test given foodRequest object with null food quantityStock when create then throw CustomException")
    public void testCreateWithNullFoodQuantityStock(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("whisky",30.0,null,"bebidas");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the quantity stock cannot be null"));
    }

    @Test
    @Order(6)
    @DisplayName("integration test given foodRequest object with Empty category name when create then throw CustomException")
    public void testCreateWithEmptyFoodCategoryName(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("whisky",30.0,10,"");
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the category name cannot be empty or null"));
    }

    @Test
    @Order(7)
    @DisplayName("integration test given foodRequest object with null categoryName when create then throw CustomException")
    public void testCreateWithNullFoodCategoryName(){
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        FoodDto food = new FoodDto("whisky",30.0,10,null);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(food)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the category name cannot be empty or null"));
    }

    @Test
    @Order(8)
    @DisplayName("integration test given categoryName when findByCategory then return FoodResponse list")
    public void testFindByCategory() throws JsonProcessingException {
        var response = given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .param("category","bebidas")
                .when()
                .get("/category")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();
        var list = objectMapper.readValue(response, List.class);
        assertThat(list)
                .extracting("name","price","quantityStock","categoryName")
                .contains(
                        tuple("white horse",30.00,10,"bebidas"),
                        tuple("whisky",30.00,10,"bebidas"));
    }

    @Test
    @Order(9)
    @DisplayName("integration test given foodRequest object and foodId when update then return HttpStatus isNoContent")
    public void testUpdate(){
        food = foodRepository.findByName("whisky").get();
        assertThat(food).isNotNull();

        FoodRequestUpdate request = new FoodRequestUpdate("whisky",35.0,15,"bebidas");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",food.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    @DisplayName("integration test given foodRequest object and invalid foodId when update then throw ObjectNotFoundException")
    public void testUpdateWithInvalidFoodId(){
        FoodRequestUpdate request = new FoodRequestUpdate("whisky",35.0,15,"bebidas");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        var message = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",100L)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(message).isEqualTo("The food cannot found");
    }

    @Test
    @Order(11)
    @DisplayName("integration test given foodRequest object with foodName contains only spaces and foodId when update then throw CustomException")
    public void testUpdateWithFoodNameContainsOnlySpaces(){
        food = foodRepository.findByName("whisky").get();
        assertThat(food).isNotNull();

        FoodRequestUpdate request = new FoodRequestUpdate("    ",35.0,15,"bebidas");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",food.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the food name cannot be empty"));
    }

    @Test
    @Order(12)
    @DisplayName("integration test given foodRequest object with categoryName contains only spaces and foodId when update then return HttpStatus isNoContent")
    public void testUpdateWithFoodCategoryNameContainsOnlySpaces(){
        food = foodRepository.findByName("whisky").get();
        assertThat(food).isNotNull();

        FoodRequestUpdate request = new FoodRequestUpdate("whisky",35.0,15,"   ");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",food.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("the category name cannot be empty"));
    }


    @Test
    @Order(13)
    @DisplayName("integration test given foodId when delete then return HttpStatus isNoContent")
    public void testDelete(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        given().spec(specification)
                .header("Authorization","Bearer "+ token)
                .when()
                .delete("/delete/{id}",food.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(14)
    @DisplayName("integration test given invalid foodId when delete then throw ObjectNotFoundException")
    public void testDeleteWithInvalidFoodId(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        var message = given().spec(specification)
                .header("Authorization","Bearer "+ token)
                .when()
                .delete("/delete/{id}",food.getId())
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(message).isEqualTo("The food cannot found");
    }
}
