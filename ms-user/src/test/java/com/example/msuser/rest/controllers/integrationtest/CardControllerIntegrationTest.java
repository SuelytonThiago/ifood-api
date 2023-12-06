package com.example.msuser.rest.controllers.integrationtest;
import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.msuser.domain.entities.Cards;
import com.example.msuser.rest.dto.CardRequestDto;
import com.example.msuser.rest.dto.CardRequestUpdate;
import com.example.msuser.rest.dto.UserLoginDto;
import com.fasterxml.jackson.core.type.TypeReference;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
public class CardControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static UserLoginDto userLogin;
    private static ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private CardRequestDto cardRequest;
    private CardRequestUpdate cardRequestUpdate;
    private static Long cardId;

    @BeforeAll
    private static void beforeAll(){
        objectMapper = new ObjectMapper();

        userLogin = new UserLoginDto("ana@example.com","senha123");

        specification = new RequestSpecBuilder()
                .setBasePath("/api/cards")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @BeforeEach
    public void beforeEach(){
        restTemplate = new RestTemplate();
        cardRequest = new CardRequestDto(
                "myCard",
                "1234567891234567",
                "12/2028",
                "266",
                1);
        cardRequestUpdate = new CardRequestUpdate("myNewCard",
                "1231231231231231",
                "12/2028",
                "266",
                2);
    }

    @Test
    @Order(1)
    @DisplayName("integration Test given cardRequest Object When CreateNewCard then Return Status 201")
    public void integrationTestCreateNewCard(){

        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(cardRequest)
                .when()
                .post("/create")
                .then().statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration Test given When FindAllPaymentMethods then Return CardList")
    public void integrationTestFindAllPaymentMethods() throws IOException {
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        var response = given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when()
                .get("/paymentMethods")
                .then()
                .statusCode(200).extract().body().asString();
        List<Cards> cardsList = objectMapper.readValue(response, List.class);
        assertThat(cardsList.size()).isEqualTo(1);
        assertThat(cardsList)
                .extracting("name","type")
                .contains(
                        tuple("myCard","CREDIT_CARD")
                );
        String json = objectMapper.writeValueAsString(cardsList);
        List<Cards> list = objectMapper.readValue(json,new TypeReference<List<Cards>>() {});
        cardId = list.get(0).getId();
    }


    @Test
    @Order(3)
    @DisplayName("integration Test given With CardRequestUpdate Object And CardId When Update Then Return Status 204")
    public void integrationTestUpdate(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(cardRequestUpdate)
                .when()
                .patch("/update/{id}",cardId)
                .then().statusCode(204);
    }

    @Test
    @Order(4)
    @DisplayName("integration Test given With CardRequestUpdate Object And Invalid CardId When Update Then Return Status 204")
    public void integrationTestUpdateWithInvalidCardId(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(cardRequestUpdate)
                .when()
                .patch("/update/{id}",5L)
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName("integration Test given With Invalid CardId When Delete Then Return Status 404")
    public void integrationTestDeleteWithInvalidCardId(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when().delete("/delete/{id}",5L)
                .then().statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("integration Test given CardId When Delete Then Return Status 204")
    public void integrationTestDelete(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when().delete("/delete/{id}",cardId)
                .then().statusCode(204);
    }

    @Test
    @Order(8)
    @DisplayName("integration Test given Empty UserCardList When FindAllPaymentMethods then Return UserCardList")
    public void integrationTestFindAllPaymentMethodsWithEmptyUserCardList () throws IOException {
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when()
                .get("/paymentMethods")
                .then()
                .statusCode(400);
    }
}
