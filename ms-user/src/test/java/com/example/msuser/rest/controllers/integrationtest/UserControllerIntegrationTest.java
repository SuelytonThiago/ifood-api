package com.example.msuser.rest.controllers.integrationtest;
import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.msuser.rest.dto.UserLoginDto;
import com.example.msuser.rest.dto.UserRequestDto;
import com.example.msuser.rest.dto.UserRequestUpdate;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static UserLoginDto userLogin;
    private RestTemplate restTemplate;
    private UserRequestDto userRequest;
    private UserRequestUpdate userRequestUpdate;

    @BeforeAll
    public static void setUp(){
        userLogin = new UserLoginDto("janaina@example.com","senha123");

        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        specification = new RequestSpecBuilder()
                .setBasePath("/api/users")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @BeforeEach
    public void beforeEach(){
        restTemplate = new RestTemplate();
        userRequest = new UserRequestDto("janaina",
                "janaina@example.com",
                "99940028922",
                "35313361007",
                "senha123");

        userRequestUpdate = new UserRequestUpdate("janaina",
                "janaina0202@example.com",
                "99940027944",
                "senha123");

    }


    @Test
    @Order(1)
    @DisplayName("integration Test given UserRequestObject when CreteNewUser Then Return Status 201 ")
    public void integrationTestCreateNewUser(){
        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(userRequest)
                .when()
                .post("/create")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration Test given UserRequestObject with Email already in use when CreteNewUser Then Throw Status 409 ")
    public void integrationTestCreateNewUserWithEmailAlreadyInUse(){
        UserRequestDto requestDto = new UserRequestDto(
                "jaime",
                "ana@example.com",
                "99940028922",
                "30975093053",
                "senha123");

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(requestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(3)
    @DisplayName("integration Test given UserRequestObject with cpf already in use when CreteNewUser Then Throw Status 409 ")
    public void integrationTestCreateNewUserWithCpfAlreadyInUse(){
        UserRequestDto requestDto = new UserRequestDto(
                "jaime",
                "jaime@example.com",
                "99940028922",
                "61254591010",
                "senha123");

        given().spec(specification)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(requestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(4)
    @DisplayName("integration test given UserRequestUpdate With User Email Already In Use Object When Update Then Return Status 409")
    public void integrationTestUpdateWithUserEmailAlreadyInUse(){
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        UserRequestUpdate requestUpdate = new UserRequestUpdate(
                "janaina",
                "ana@example.com",
                "99940027944",
                "senha123");

        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(requestUpdate)
                .when()
                .patch("/update")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(5)
    @DisplayName("integration test given UserRequestUpdate Object When Update Then Return Status 204")
    public void integrationTestUpdate(){
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(userRequestUpdate)
                .when()
                .patch("/update")
                .then()
                .statusCode(204);
    }
}
