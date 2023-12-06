package com.example.msorder.rest.controllers.integrationtests;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.address.AddressResponse;
import com.example.card.CardResponse;
import com.example.food.CategoryResponse;
import com.example.food.FoodResponse;
import com.example.food.StoreResponse;
import com.example.msorder.config.ConstantsTest;
import com.example.msorder.config.ContainerBase;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.repositories.BagRepository;
import com.example.msorder.domain.repositories.OrdersRepository;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.grpc.client.MyUserGrpcClient;
import com.example.msorder.rest.dto.*;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderControllerIntegrationTest extends ContainerBase {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static CardResponse cardResponse;
    private static AddressResponse addressResponse;
    private static CategoryResponse category;
    private static StoreResponse store;
    private static FoodResponse food;
    private static OrderRequestDto orderRequestDto;
    private static Orders order;
    private static OrderUserInfo orderUserInfo;
    private static String userToken,admToken;
    private static UserResponse userResponse;
    private static RoleResponse userRole;
    private static List<RoleResponse> userRoles;
    private static RoleResponseList userAuthorities;
    private static UserResponse adminResponse;
    private static RoleResponse adminRole;
    private static List<RoleResponse> adminRoles;
    private static RoleResponseList adminAuthorities;
    private static Bag bag;

    @Autowired
    private OrdersRepository ordersRepository;
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
                .setBasePath("/api/orders")
                .setPort(ConstantsTest.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        cardResponse = CardResponse.newBuilder()
                .setId(1L)
                .setName("MyCard")
                .setNumber("1234567891234567")
                .setCvv("266")
                .setExpiration("12/2028")
                .setTypeCard("CREDIT_CARD").build();
        addressResponse = AddressResponse.newBuilder()
                .setId(1L)
                .setStreet("rua a")
                .setNeighborhood("bairro a")
                .setCity("cidade a")
                .setState("estado a")
                .setCep("00000000")
                .build();

        orderRequestDto = new OrderRequestDto(1L,1L);
        orderUserInfo = new OrderUserInfo(addressResponse.getStreet(),
                addressResponse.getNeighborhood(),
                addressResponse.getCity(),
                addressResponse.getState(),
                addressResponse.getCep(),
                cardResponse.getTypeCard());

        userRole = RoleResponse.newBuilder()
                .setId(25L)
                .setRoleName("ROLE_USER")
                .build();
        userRoles = new ArrayList<>();
        userRoles.add(userRole);
        userAuthorities = RoleResponseList.newBuilder().addAllRoles(userRoles).build();
        userResponse = UserResponse.newBuilder()
                .setEmail("user@example.com")
                .setId(25L)
                .setRoles(userAuthorities).build();

        userToken = JWT.create().withSubject("user@example.com")
                .withClaim("id",25L)
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

        admToken = JWT.create().withSubject("adm@example.com")
                .withClaim("id",1L)
                .withExpiresAt(new Date(System.currentTimeMillis() + 600000))
                .sign(Algorithm.HMAC512("dasklbfnjasdfklhjdalkshvjf"));

        category = CategoryResponse.newBuilder().setName("hamburguer").build();
        store = StoreResponse.newBuilder().setId(3L).setName("bobs").build();
        food = FoodResponse.newBuilder().setCategory(category)
                .setStore(store).setName("x-tudo")
                .setPrice(18.00).setQuantityStock(15).setId(10L).build();
    }

    @Test
    @Order(1)
    @DisplayName("test given orderRequest object and HttpServlet when create then return httpStatus 201")
    public void integrationTestCreateNewOrder(){
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(food);

        bag = bagRepository.save(new Bag(null,food.getId(),store.getName(),2,food.getPrice(),userResponse.getId(), Instant.now()));
        assertThat(bag.getId()).isNotNull();

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myCardGrpcClient.findCardById(anyLong())).thenReturn(cardResponse);
        when(myAddressGrpcClient.findAddressById(anyLong())).thenReturn(addressResponse);

        given().spec(specification)
                .header("Authorization","Bearer " + userToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(orderRequestDto)
                .when()
                .post("/create")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(2)
    @DisplayName("test given orderRequest object with null addressId and HttpServlet when create then return httpStatus 400 ")
    public void integrationTestWithNullAddressId(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);

        OrderRequestDto request = new OrderRequestDto(null,1L);
        given().spec(specification)
                .header("Authorization","Bearer " + userToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message",hasItem("the addressId cannot be null"));
    }

    @Test
    @Order(3)
    @DisplayName("test given orderRequest object with null paymentMethod and HttpServlet when create then return httpStatus 400 ")
    public void integrationTestWithNullPaymentMethod(){
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);

        OrderRequestDto request = new OrderRequestDto(1L,null);
        given().spec(specification)
                .header("Authorization","Bearer " + userToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .post("/create")
                .then()
                .statusCode(400)
                .body("Message",hasItem("the paymentMethod cannot be null"));
    }

    @Test
    @Order(4)
    @DisplayName("test when findAllOrdersByUserId then return OrderResponseDto list")
    public void integrationTestFindAllOrdersByUserId() throws JsonProcessingException {
        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(userResponse);
        when(myFoodGrpcClient.findById(anyLong())).thenReturn(food);

        var content = given().spec(specification)
                .header("Authorization","Bearer " + userToken)
                .when()
                .get("/getAll")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        var list = objectMapper.readValue(content, List.class);
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    @Order(6)
    @DisplayName("test given orderStatusUpdate object when update then return HttpStatus 204")
    public void integrationTestUpdate(){
        var listOrder = ordersRepository.findAll();
        order = listOrder.get(listOrder.size()-1);
        assertThat(order).isNotNull();

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(adminResponse);
        var request = new OrderStatusUpdate(2  ,order.getId());

        given().spec(specification)
                .header("Authorization","Bearer " + admToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    @DisplayName("test given orderStatusUpdate object with null statusCode when update then return HttpStatus 400")
    public void integrationTestUpdateWithNullStatusCode(){
        var request = new OrderStatusUpdate();
        request.setOrderId(order.getId());

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(adminResponse);

        var content =  given().spec(specification)
                .header("Authorization","Bearer " + admToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update")
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");
        assertThat(content).isEqualTo("the type cod is invalid");
    }

    @Test
    @Order(8)
    @DisplayName("test given orderStatusUpdate object with null OrderId when update then return HttpStatus 400")
    public void integrationTestUpdateWithNullOrderId(){
        var request = new OrderStatusUpdate(2,null);

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(adminResponse);

        var content =  given().spec(specification)
                .header("Authorization","Bearer " + admToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update")
                .then()
                .statusCode(400)
                .body("Message",hasItem("the order id cannot be null"));
    }

    @Test
    @Order(9)
    @DisplayName("test given orderStatusUpdate object with invalid orderId when update then return HttpStatus 404")
    public void integrationTestUpdateWithInvalidOrderId(){
        ordersRepository.delete(order);
        assertThat(ordersRepository.findById(order.getId()).isEmpty()).isTrue();

        var request = new OrderStatusUpdate(2,order.getId());

        when(myUserGrpcClient.findUserByEmail(anyString())).thenReturn(adminResponse);

        var content =  given().spec(specification)
                .header("Authorization","Bearer " + admToken)
                .contentType(ConstantsTest.CONTENT_TYPE_JSON)
                .body(request)
                .when()
                .patch("/update")
                .then()
                .statusCode(404)
                .extract()
                .jsonPath()
                .getString("Message");

        assertThat(content).isEqualTo("The order is not found");
    }
}
