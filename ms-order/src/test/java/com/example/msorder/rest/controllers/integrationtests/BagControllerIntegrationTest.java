package com.example.msorder.rest.controllers.integrationtests;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.food.CategoryResponse;
import com.example.food.FoodResponse;
import com.example.food.StoreResponse;
import com.example.msorder.config.ConstantsTest;
import com.example.msorder.config.ContainerBase;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.repositories.BagRepository;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.grpc.client.MyUserGrpcClient;
import com.example.msorder.rest.dto.BagRequestDto;
import com.example.msorder.rest.dto.BagRequestUpdate;
import com.example.msorder.rest.dto.BagResponseDto;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BagControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static Bag bag;
    private static BagRequestDto bagRequestDto;
    private static BagRequestUpdate bagRequestUpdate;
    private static FoodResponse foodResponse;
    private static CategoryResponse categoryResponse;
    private static StoreResponse storeResponse;
    private static String token;
    private static UserResponse userResponse;
    private static RoleResponse userRole;
    private static List<RoleResponse> userRoles;
    private static RoleResponseList userAuthorities;


    @Autowired
    private BagRepository bagRepository;
    @MockBean
    private MyAddressGrpcClient myAddressGrpcClient;
    @MockBean
    private MyCardGrpcClient myCardGrpcClient;
    @MockBean
    private MyFoodGrpcClient myFoodGrpcClient;
    @MockBean
    private MyUserGrpcClient myUserGrpcClient;



    @BeforeAll
    public static void beforeAll(){

        objectMapper = new ObjectMapper();

        specification = new RequestSpecBuilder()
                .setBasePath("/api/bag")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        bagRequestDto = new BagRequestDto(
                1L,
                3
        );

        bagRequestUpdate = new BagRequestUpdate(2);

        categoryResponse = categoryResponse.newBuilder()
                .setName("hamburguer")
                .build();
        storeResponse = StoreResponse.newBuilder()
                .setId(1L)
                .setName("habbibs")
                .build();
        foodResponse = FoodResponse.newBuilder()
                .setCategory(categoryResponse)
                .setStore(storeResponse)
                .setName("x-tudo")
                .setPrice(18.00)
                .setQuantityStock(15)
                .setId(1L)
                .build();

        userRole = RoleResponse.newBuilder()
                .setId(3L)
                .setRoleName("ROLE_USER")
                .build();
        userRoles = new ArrayList<>();
        userRoles.add(userRole);
        userAuthorities = RoleResponseList.newBuilder().addAllRoles(userRoles).build();
        userResponse = UserResponse.newBuilder()
                .setEmail("user@example.com")
                .setId(10L)
                .setRoles(userAuthorities).build();

        token = JWT.create().withSubject("user@example.com")
                .withClaim("id",10L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));
    }

    @Test
    @Order(1)
    @DisplayName("test given bagRequest object when create then return status 201 ")
    public void integrationTestCreate(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(foodResponse);

        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(bagRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("test given bagRequest FoodStore Different( object when create then return status 400 ")
    public void integrationTestCreateWithFoodStoreDifferent(){
        var cat = CategoryResponse.newBuilder().setName("hamburguer").build();
        var store = StoreResponse.newBuilder().setId(3L).setName("bobs").build();
        var food = FoodResponse.newBuilder().setCategory(cat)
                .setStore(store).setName("x-tudo")
                .setPrice(18.00).setQuantityStock(15).setId(10L).build();
        var request = new BagRequestDto(10L,3);
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(food);
        
        var  content = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("There cannot be items from different restaurants in the bag");
    }


    @Test
    @Order(3)
    @DisplayName("test given bagRequest object with null foodId when create then return status 400")
    public void integrationTestCreateWithNullFoodId(){
        BagRequestDto request = new BagRequestDto(null, 3);
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(foodResponse);

        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message",hasItem("the food id cannot be empty or null"));
    }

    @Test
    @Order(4)
    @DisplayName("test given bagRequest object with null quantity when create then return status 400")
    public void integrationTestCreateWithNullQuantity(){
        BagRequestDto request = new BagRequestDto(1L, null);
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(foodResponse);

        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message",hasItem("the quantity cannot be empty or null"));
    }

    @Test
    @Order(5)
    @DisplayName("test when getBagItems then return BagResponseList")
    public void integrationTestGetBagItems() throws JsonProcessingException {
        var bagList = bagRepository.findAll();
        assertThat(bagList.isEmpty()).isFalse();

        bag = bagList.get(bagList.size() - 1);

        var bagDto = new BagResponseDto(bag);
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().body().asString();
        var list = objectMapper.readValue(content, List.class);
        assertThat(list)
                .extracting("foodId","quantity","price")
                .contains(
                        tuple(1,3,18.0)
                );
    }

    @Test
    @Order(6)
    @DisplayName("test given bgRequestUpdate object and bagId when update will update bag data")
    public void integrationTestUpdate(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(bagRequestUpdate)
                .when()
                .patch("/update/{id}",bag.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    @DisplayName("test given bagId when delete then return HttpStatus 204")
    public void integrationTestDelete(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .delete("/delete/{id}",bag.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(8)
    @DisplayName("test given invalid bagId when delete then return HttpStatus 204")
    public void integrationTestDeleteWithInvalidBagId(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        var response = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .delete("/delete/{id}",bag.getId())
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(response).isEqualTo("the bag item is not found");
    }

    @Test
    @Order(9)
    @DisplayName("test given invalid bagId RequestUpdate object and bagId when update then return HttpStatus 404")
    public void integrationTestUpdateWithInvalidBagId(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(bagRequestUpdate)
                .when()
                .patch("/update/{id}",bag.getId())
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("the bag item is not found");
    }

    @Test
    @Order(10)
    @DisplayName("test With Null Bag Items when getBagItems then return httpStatus 400")
    public void integrationTestGetBagItemsWithNullBagItems() throws JsonProcessingException {

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .get()
                .then()
                .statusCode(400)
                .extract().jsonPath().getString("Message");

        assertThat(content).isEqualTo("the bag is empty");
    }


}
