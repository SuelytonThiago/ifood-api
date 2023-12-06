package com.example.msuser.rest.controllers.integrationtest;
import com.example.msuser.configs.ConstantsTest;
import com.example.msuser.configs.ContainerBase;
import com.example.msuser.domain.entities.Address;
import com.example.msuser.rest.dto.AddressRequestDto;
import com.example.msuser.rest.dto.AddressRequestUpdate;
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

import static org.assertj.core.api.Assertions.*;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private UserLoginDto userLogin;
    private RestTemplate restTemplate;
    private AddressRequestDto addressRequestDto;
    private AddressRequestUpdate addressRequestUpdate;
    private static Long addressId;



    @BeforeAll
    public static void beforeAll(){

        objectMapper = new ObjectMapper();

        specification = new RequestSpecBuilder()
                .setBasePath("/api/address")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        restTemplate = new RestTemplate();
        addressRequestDto = new AddressRequestDto(
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000");
        addressRequestUpdate = new AddressRequestUpdate(
                "rua a",
                "bairro b",
                "cidade b",
                "estado b",
                "11111111");
        userLogin = new UserLoginDto("ana@example.com", "senha123");
    }

    @Test
    @Order(1)
    @DisplayName("integration Test given AddressRequest Object When CreateNewAddress then Return Status 201")
    public void integrationTestCreateNewAddress(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;

        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(addressRequestDto)
                .when()
                .post("/create")
                .then().statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("integration Test given When FindAllAddressesMethods then Return AddressList")
    public void integrationTestFindAllAddresses() throws IOException {
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        var response = given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when()
                .get("/myAddresses")
                .then()
                .statusCode(200).extract().body().asString();
        var addressList = objectMapper.readValue(response,List.class);
        assertThat(addressList.size()).isEqualTo(1);
        assertThat(addressList)
                .extracting("street","neighborhood","city","state","cep")
                .contains(
                        tuple("rua a","bairro a","cidade a","estado a","00000000")
                );
        String json = objectMapper.writeValueAsString(addressList);
        List<Address> list = objectMapper.readValue(json,new TypeReference<List<Address>>() {});
        addressId = list.get(0).getId();
    }


    @Test
    @Order(3)
    @DisplayName("integration Test given With AddressRequestUpdate Object And AddressId When Update Then Return Status 204")
    public void integrationTestUpdate(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(addressRequestUpdate)
                .when()
                .patch("/update/{id}",addressId)
                .then().statusCode(204);
    }



    @Test
    @Order(4)
    @DisplayName("integration Test given With AddressRequestUpdate Object And Invalid AddressId When Update Then Return Status 204")
    public void integrationTestUpdateWithInvalidId(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(addressRequestUpdate)
                .when()
                .patch("/update/{id}",5L)
                .then().statusCode(404);
    }

    @Test
    @Order(5)
    @DisplayName("integration Test given With Invalid AddressId When Delete Then Return Status 404")
    public void integrationTestDeleteWithInvalidAddressId(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when().delete("/delete/{id}",5L)
                .then().statusCode(404);
    }

    @Test
    @Order(6)
    @DisplayName("integration Test given AddressId When Delete Then Return Status 204")
    public void integrationTestDelete(){
        Map<String,String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL,userLogin,Map.class);
        assert  tokens != null;
        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when().delete("/delete/{id}",addressId)
                .then().statusCode(204);
    }

    @Test
    @Order(7)
    @DisplayName("integration Test given Empty UserAddress List When FindAllAddressesMethods then Return UserAddressList")
    public void integrationTestFindAllAddressesMethodsWithEmptyUserAddressList () throws IOException {
        Map<String, String> tokens = restTemplate.postForObject(ConstantsTest.LOGIN_URL, userLogin, Map.class);
        assert tokens != null;

        given().spec(specification)
                .header("Authorization","Bearer "+ tokens.get("access_token"))
                .when()
                .get("/myAddresses")
                .then()
                .statusCode(400);
    }
}
