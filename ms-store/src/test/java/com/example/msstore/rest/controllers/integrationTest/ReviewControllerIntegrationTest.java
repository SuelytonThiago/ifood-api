package com.example.msstore.rest.controllers.integrationTest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.msstore.config.ConstantsTest;
import com.example.msstore.config.ContainerBase;
import com.example.msstore.domain.entities.Review;
import com.example.msstore.domain.repositories.ReviewRepository;
import com.example.msstore.domain.repositories.StoreRepository;
import com.example.msstore.grpc.client.UserServiceGrpcClient;
import com.example.msstore.rest.dto.ReviewRequestDto;
import com.example.msstore.rest.dto.ReviewRequestUpdate;
import com.example.user.RoleResponse;
import com.example.user.RoleResponseList;
import com.example.user.UserResponse;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class ReviewControllerIntegrationTest extends ContainerBase {


    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static Review review;
    private static String token;
    private static UserResponse userResponse;
    private static RoleResponse userRole;
    private static List<RoleResponse> userRoles;
    private static RoleResponseList userAuthorities;

    @MockBean
    private UserServiceGrpcClient grpcClient;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    @BeforeAll
    public static void beforeAll(){
        objectMapper = new ObjectMapper();
        specification = new RequestSpecBuilder()
                .setBasePath("/api/reviews")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        token = JWT.create().withSubject("user@example.com")
                .withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));

        userRole = RoleResponse.newBuilder()
                .setId(3L)
                .setRoleName("ROLE_USER")
                .build();
        userRoles = new ArrayList<>();
        userRoles.add(userRole);
        userAuthorities = RoleResponseList.newBuilder().addAllRoles(userRoles).build();
        userResponse = UserResponse.newBuilder()
                .setEmail("user@example.com")
                .setId(1L)
                .setRoles(userAuthorities).build();
    }

    @Test
    @Order(1)
    @DisplayName("integration test given reviewRequest object when create then return HttpStatus isCreated")
    public void integrationTestCreate(){
        ReviewRequestDto request = new ReviewRequestDto(3,"mais ou menos");
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create/{id}",store.getId())
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration test given reviewRequest object with null rating when create then throw CustomException ")
    public void integrationTestCreateWithNullRating(){
        ReviewRequestDto request = new ReviewRequestDto(null,"mais ou menos");
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create/{id}",store.getName())
                .then()
                .statusCode(400)
                .body("Message", hasItem("insert a rating to establishment"));
    }

    @Test
    @Order(4)
    @DisplayName("integration test given reviewRequest object with null comment when create then throw CustomException ")
    public void integrationTestCreateWithNullComment(){
        ReviewRequestDto request = new ReviewRequestDto(3,null);
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create/{id}",store.getName())
                .then()
                .statusCode(400)
                .body("Message", hasItem("insert a comment to establishment"));
    }

    @Test
    @Order(5)
    @DisplayName("integration test given reviewRequest object with empty comment when create then throw CustomException ")
    public void integrationTestCreateWithEmptyComment(){
        ReviewRequestDto request = new ReviewRequestDto(3,"");
        var store = storeRepository.findByName("habbibs").get();
        assertThat(store).isNotNull();
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create/{id}",store.getName())
                .then()
                .statusCode(400)
                .body("Message", hasItem("insert a comment to establishment"));
    }

    @Test
    @Order(6)
    @DisplayName("integration test given reviewId and reviewRequest object when update then return HttpStatus isNoContent")
    public void integrationTestUpdate(){
        ReviewRequestUpdate request = new ReviewRequestUpdate(4, "bom");
        var listReview = reviewRepository.findAll();
        review = listReview.get(listReview.size() -1);

        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",review.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    @DisplayName("integration test given invalid reviewId and reviewRequest object when update then throw ObjectNotFoundException")
    public void integrationTestUpdateWithInvalidReviewId(){
        ReviewRequestUpdate request = new ReviewRequestUpdate(4, "bom");

        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
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
        assertThat(message).isEqualTo("The review is not found");
    }

    @Test
    @Order(8)
    @DisplayName("integration test given reviewId and reviewRequest object with comment contain only spaces when update then throw CustomException")
    public void integrationTestUpdateWithCommentContainsOnlySpaces(){
        ReviewRequestUpdate request = new ReviewRequestUpdate(4, "  ");

        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        var message = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update/{id}",review.getId())
                .then()
                .statusCode(400)
                .body("Message", hasItem("this comment cannot be only spaces"));
    }

    @Test
    @Order(9)
    @DisplayName("integration test given reviewId when delete then return HttpStatus isNoContent")
    public void integrationTestDelete(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .delete("/delete/{id}",review.getId())
                .then().statusCode(204);
    }

    @Test
    @Order(10)
    @DisplayName("integration test given invalid reviewId when delete then throw objectNotFoundException")
    public void integrationTestDeleteWithInvalidReviewId(){
        when(grpcClient.getUserByEmail(anyString())).thenReturn(userResponse);
        var message = given().spec(specification)
                .header("Authorization","Bearer " + token)
                .when()
                .delete("/delete/{id}",review.getId())
                .then().statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(message).isEqualTo("The review is not found");
    }


}
