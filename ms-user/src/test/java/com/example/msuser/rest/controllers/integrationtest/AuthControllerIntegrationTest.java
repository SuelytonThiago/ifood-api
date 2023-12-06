package com.example.msuser.rest.controllers.integrationtest;

import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.msuser.domain.entities.Roles;
import com.example.msuser.domain.repositories.RoleRepository;
import com.example.msuser.rest.dto.UserLoginDto;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static UserLoginDto userLogin;
    private RestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    private Roles role;

    @BeforeAll
    public static void beforeAll(){
        userLogin = new UserLoginDto("ana@example.com","senha123");

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/auth")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @BeforeEach
    public void beforeEach(){
        restTemplate = new RestTemplate();
    }

    @Test
    @Order(1)
    @DisplayName("integration Test Given UserLogin Object Then Login Then Return AccessToken And RefreshToken")
    public void integrationTestLogin() throws IOException {
        var content = given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(userLogin)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().body().asString();

        var response = objectMapper.readValue(content, Map.class);
        assertThat(response.get("access_token")).isNotNull();
        assertThat(response.get("refresh_token")).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test Given RefreshToken When Refresh Then Return AccessToken")
    public void integrationTestRefresh() throws IOException {
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert tokens != null;

        var content = given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("refresh_token"))
                .when()
                .post("/refresh")
                .then()
                .statusCode(200)
                .extract().body().asString();

        assertThat(content).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("integration Test Given UserLogin Object With Invalid Password Then Login Then Return AccessToken And RefreshToken")
    public void integrationTestLoginWithInvalidPassword() throws IOException {
        userLogin.setPassword("senha1234");

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(userLogin)
                .when()
                .post("/login")
                .then()
                .statusCode(400);


    }

    @Test
    @Order(4)
    @DisplayName("integration Test Given UserLogin Object With Invalid Email Then Login Then Return AccessToken And RefreshToken")
    public void integrationTestLoginWithInvalidEmail() throws IOException {
        userLogin.setEmail("jo@example.com");
        userLogin.setPassword("senha123");

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(userLogin)
                .when()
                .post("/login")
                .then()
                .statusCode(404);

    }

}
