package com.example.msstore.rest.controllers.integrationTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.config.ConstantsTest;
import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Store;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rest.dto.*;
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
public class StoreControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static String tokenOwner,tokenAdmin;

    private static Store store;
    private static UserResponse adminResponse;
    private static RoleResponse adminRole;
    private static List<RoleResponse> adminRoles;
    private static RoleResponseList adminAuthorities;

    private static UserResponse ownerResponse;
    private static RoleResponse ownerRole;
    private static List<RoleResponse> ownerRoles;
    private static RoleResponseList ownerAuthorities;

    @Autowired
    private StoreRepository storeRepository;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @BeforeAll
    public static void beforeAll(){

        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/api/store")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        tokenOwner = JWT.create().withSubject("stive@example.com")
                .withClaim("id",10L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));

        tokenAdmin = JWT.create().withSubject("adm@example.com")
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

        ownerRole = RoleResponse.newBuilder()
                .setId(2L)
                .setRoleName("ROLE_OWNER")
                .build();
        ownerRoles = new ArrayList<>();
        ownerRoles.add(ownerRole);
        ownerAuthorities = RoleResponseList.newBuilder().addAllRoles(ownerRoles).build();
        ownerResponse = UserResponse.newBuilder()
                .setEmail("stive@example.com")
                .setId(10L)
                .setRoles(ownerAuthorities).build();


    }

    @Test
    @Order(1)
    @DisplayName("integration test given storeRequest object when createNewStore then return HttpStatus isCreated")
    public void integrationTestCreateNewStore(){

        StoreRequestDto storeRequestDto = new StoreRequestDto("mais saude","abc",1,"stive","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        when(grpcClient.createOwner(any(OwnerRequestDto.class))).thenReturn(10L);

        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(201);

    }

    @Test
    @Order(2)
    @DisplayName("integration test given storeRequest object with null storeName when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullStoreName(){
        StoreRequestDto storeRequestDto = new StoreRequestDto(null,"abc",3,"stive","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the name cannot be empty or null"));

    }

    @Test
    @Order(3)
    @DisplayName("integration test given storeRequest object with Empty storeName when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithEmptyStoreName(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("","abc",3,"stive","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the name cannot be empty or null"));

    }

    @Test
    @Order(3)
    @DisplayName("integration test given storeRequest object with null bio when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullBio(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac",null,3,"stive","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the bio cannot be empty or null"));

    }

    @Test
    @Order(4)
    @DisplayName("integration test given storeRequest object with Empty bio when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithEmptyBio(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","",3,"stive","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the bio cannot be empty or null"));

    }

    @Test
    @Order(5)
    @DisplayName("integration test given storeRequest object with null ownerName when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullOwnerName(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,null,"stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The user name cannot be empty or null"));

    }

    @Test
    @Order(6)
    @DisplayName("integration test given storeRequest object with empty ownerName when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithEmptyOwnerName(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"","stive@example.com","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The user name cannot be empty or null"));

    }

    @Test
    @Order(7)
    @DisplayName("integration test given storeRequest object with invalid email when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidEmail(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive","99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("Insert a valid email"));

    }


    @Test
    @Order(7)
    @DisplayName("integration test given storeRequest object with null email when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullEmail(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive",null,"99940028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The email cannot be Empty or null"));

    }

    @Test
    @Order(8)
    @DisplayName("integration test given storeRequest object with invalid contactNumber when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidContactNumber(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99sdasd028922","10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("Please provide us with a valid telephone number"));

    }

    @Test
    @Order(9)
    @DisplayName("integration test given storeRequest object with null contactNumber when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullContactNumber(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com",null,"10624948064","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The contact number cannot be null"));

    }

    @Test
    @Order(10)
    @DisplayName("integration test given storeRequest object with invalid cpf when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidCpf(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922","asdsad","senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("Insert a valid cpf"));

    }

    @Test
    @Order(11)
    @DisplayName("integration test given storeRequest object with null cpf when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullCpf(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922",null,"senha123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The cpf cannot be null"));

    }

    @Test
    @Order(12)
    @DisplayName("integration test given storeRequest object with Invalid Password Contain Less Than 8 Characters when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidPasswordContainLessThan8Characters(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922","10624948064","sen");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the password must contain letters and numbers,and have at least 8 characters"));

    }

    @Test
    @Order(13)
    @DisplayName("integration test given storeRequest object with Invalid Password Contain Only Letters when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidPasswordContainOnlyLetters(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922","10624948064","senasdasddas");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the password must contain letters and numbers,and have at least 8 characters"));

    }

    @Test
    @Order(13)
    @DisplayName("integration test given storeRequest object with Invalid Password Contain Only Numbers when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithInvalidPasswordContainOnlyNumbers(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922","10624948064","789456123");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("the password must contain letters and numbers,and have at least 8 characters"));

    }

    @Test
    @Order(14)
    @DisplayName("integration test given storeRequest object with null Password when createNewStore then return HttpStatus isBadRequest")
    public void integrationTestCreateNewStoreWithNullPassword(){
        StoreRequestDto storeRequestDto = new StoreRequestDto("mac","abc",3,"stive","stive@example.com","99940028922","10624948064",null);
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(storeRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message", hasItem("The password cannot be null"));

    }

    @Test
    @Order(15)
    @DisplayName("integration test when findAllByPharmacy then return PharmacyList")
    public void integrationTestFindAllByPharmacy() throws JsonProcessingException {
        store = storeRepository.findByName("mais saude").get();
        assertThat(store).isNotNull();
        var storeDto = new StoreResponseDto(store);
        var content = given().spec(specification)
                .when()
                .get("/pharmacy")
                .then()
                .statusCode(200)
                .extract().body().asString();
        var list = objectMapper.readValue(content, List.class);
        assertThat(list)
                .extracting("name","bio","totalRating")
                .contains(
                        tuple(storeDto.getName(),storeDto.getBio(),storeDto.getTotalRating())
                );
    }

    @Test
    @Order(16)
    @DisplayName("integration test when findAllBySupermarket then return SupermarketList")
    public void integrationTestFindAllBySupermarket() throws JsonProcessingException {
        var content = given().spec(specification)
                .when()
                .get("/supermarket")
                .then()
                .statusCode(200)
                .extract().body().asString();
        var list = objectMapper.readValue(content, List.class);
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    @Order(16)
    @DisplayName("integration test when findAllByRestaurant then return RestaurantList")
    public void integrationTestFindAllByRestaurant() throws JsonProcessingException {
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();
        var storeDto = new StoreResponseDto(store);
        var content = given().spec(specification)
                .when()
                .get("/restaurant")
                .then()
                .statusCode(200)
                .extract().body().asString();
        var list = objectMapper.readValue(content, List.class);
        assertThat(list)
                .extracting("name","bio","totalRating")
                .contains(
                        tuple(storeDto.getName(),storeDto.getBio(),storeDto.getTotalRating())
                );
    }

    @Test
    @Order(17)
    @DisplayName("integration test given store name when findByName then return StoreResponse")
    public void integrationTestFindByName() throws JsonProcessingException {
        var storeDto = new StoreResponseDto(store);
        var content = given()
                .spec(specification)
                .param("storeName",store.getName())
                .when()
                .get("/search")
                .then()
                .statusCode(200)
                .extract()
                .body().asString();
        var response = objectMapper.readValue(content, StoreResponseDto.class);
        assertThat(response).usingRecursiveComparison().isEqualTo(storeDto);
    }

    @Test
    @Order(18)
    @DisplayName("integration test given invalid store name when findByName then throw ObjectNotfoundException")
    public void integrationTestFindByNameWithInvalidStoreName() throws JsonProcessingException {
        var storeDto = new StoreResponseDto(store);
        var content = given()
                .spec(specification)
                .param("storeName","asdsadsa")
                .when()
                .get("/search")
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("The store not found");
    }

    @Test
    @Order(19)
    @DisplayName("integration test given store id when getReviews then return store ReviewsResponseList")
    public void integrationTestGetReviews() throws JsonProcessingException {
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        var reviews = store.getReviews();
        var reviewDto = new ReviewResponseDto(reviews.get(0));

        var content = given()
                .spec(specification)
                .when()
                .get("/reviews/{id}",store.getId())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        var list = objectMapper.readValue(content, List.class);
        assertThat(list)
                .extracting("rating","comment","date")
                .contains(
                        tuple(reviewDto.getRating(),reviewDto.getComment(),reviewDto.getDate())
                );
    }

    @Test
    @Order(19)
    @DisplayName("integration test given invalid store id when getReviews then throw ObjectNotFoundException")
    public void integrationTestGetReviewsWithInvalidStoreId() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .when()
                .get("/reviews/{id}",100L)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("The store not found");
    }

    @Test
    @Order(20)
    @DisplayName("integration test given storeId when getStoreMenu Then Return store FoodResponseList")
    public void integrationTestGetStoreMenu() throws JsonProcessingException {
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();

        var menu = store.getMenu();
        var foodDto = new FoodDto(menu.get(0));

        var content = given()
                .spec(specification)
                .when()
                .get("/menu/{id}",store.getId())
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        var list = objectMapper.readValue(content, List.class);
        assertThat(list)
                .extracting("name","price","quantityStock","categoryName")
                .contains(
                        tuple(foodDto.getName(),foodDto.getPrice(),foodDto.getQuantityStock(),foodDto.getCategoryName())
                );
    }

    @Test
    @Order(21)
    @DisplayName("integration test given invalid storeId when getStoreMenu Then throw ObjectNotFoundException")
    public void integrationTestGetStoreMenuWithInvalidStoreId() throws JsonProcessingException {
        var content = given()
                .spec(specification)
                .when()
                .get("/menu/{id}",100L)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("The store not found");
    }

    @Test
    @Order(22)
    @DisplayName("integration test given storeRequest object and storeId when Update then return HttpStatus isNoContent ")
    public void integrationTestUpdate(){
        var request = new StoreRequestUpdate("mais saude","abcdefgh");
        System.out.println(store.getOwnerId() == 10L);
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenOwner)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",store.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(23)
    @DisplayName("integration test given storeRequest object and invalid storeId when Update then throw ObjectNotFoundException")
    public void integrationTestUpdateWithInvalidStoreId(){
        var request = new StoreRequestUpdate("mais saude","abcdefgh");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + tokenOwner)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",100L)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("The store not found");
    }

    @Test
    @Order(24)
    @DisplayName("integration test given storeRequest object with name contain only spaces and storeId when Update then throw CustomException")
    public void integrationTestUpdateWithNameContainsOnlySpaces(){
        var request = new StoreRequestUpdate("   ","abcdefgh");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + tokenOwner)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message",hasItem("The name cannot contain only spaces"));
    }

    @Test
    @Order(25)
    @DisplayName("integration test given storeRequest object with bio contain only spaces and storeId when Update then throw CustomException")
    public void integrationTestUpdateWithBioContainsOnlySpaces(){
        var request = new StoreRequestUpdate("mais saude","   ");
        when(grpcClient.getUserByEmail(anyString())).thenReturn(ownerResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + tokenOwner)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",store.getId())
                .then()
                .statusCode(400)
                .body("Message",hasItem("The bio cannot contain only spaces"));
    }

    @Test
    @Order(26)
    @DisplayName("integration test given storeId when delete then Return HttpStatus isNoContent")
    public void integrationTestDelete(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .when()
                .delete("/delete/{id}",store.getId())
                .then()
                .statusCode(204);

    }

    @Test
    @Order(25)
    @DisplayName("integration test given invalid storeId when Delete then throw ObjectNotFoundException")
    public void integrationTestDeleteWithInvalidStoreId(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(adminResponse);
        var content = given().spec(specification)
                .header("Authorization","Bearer " + tokenAdmin)
                .when()
                .delete("/delete/{id}",100L)
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");
        assertThat(content).isEqualTo("The store not found");

    }
}
