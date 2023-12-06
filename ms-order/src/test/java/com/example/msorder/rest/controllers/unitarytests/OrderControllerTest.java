package com.example.msorder.rest.controllers.unitarytests;

import com.example.address.AddressResponse;
import com.example.card.CardResponse;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.enums.OrderStatus;
import com.example.msorder.rest.controllers.OrderController;
import com.example.msorder.rest.dto.AddressDto;
import com.example.msorder.rest.dto.OrderRequestDto;
import com.example.msorder.rest.dto.OrderResponseDto;
import com.example.msorder.rest.dto.OrderStatusUpdate;
import com.example.msorder.rest.services.OrderService;
import com.example.msorder.rest.services.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Bag bag;
    private CardResponse cardResponse;
    private AddressResponse addressResponse;
    private OrderRequestDto orderRequestDto;
    private Orders order;
    private OrderUserInfo orderUserInfo;
    private OrderResponseDto orderResponse;
    private OrderStatusUpdate orderStatusUpdate;


    @BeforeEach
    public void beforeEach(){
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .alwaysDo(print())
                .build();

        bag = new Bag(
                1L,
                1L,
                "habbibs",
                2,
                30.00,
                1L,
                Instant.now());


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

        order = new Orders(1L,
                LocalDate.now(),
                bag.getUserId(),
                bag.getStoreName(),
                orderUserInfo,
                OrderStatus.WAITING_CONFIRMATION);

        orderResponse = new OrderResponseDto(
                order.getDate().toString(),
                order.getOrderStatus().toString(),
                order.getStoreName(),
                new AddressDto(orderUserInfo),
                new ArrayList<>(),
                orderUserInfo.getPaymentMethod(),
                String.format("%.2f",order.getTotal()));

        orderStatusUpdate = new OrderStatusUpdate(2, order.getId());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test given HttpServlet and orderRequestDtoobject when create will save new order from database")
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/orders/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test given HttpServlet and orderRequestDtoobject when create will save new order from database")
    public void testCreateWithNullAddressId() throws Exception {
        orderRequestDto.setAddressId(null);
        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user",roles = "USER")
    @DisplayName("test when findAllOrdersByUserId then return OrderResponseDto list")
    public void testFindAllOrdersByUserId() throws Exception {
        var list = Collections.singletonList(orderResponse);
        given(orderService.findAllOrdersByUserId(anyLong())).willReturn(list);

        mockMvc.perform(get("/api/orders/getAll"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",is(list.size())));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("test given orderStatus object when update will update order status")
    public void testUpdate() throws Exception {
        mockMvc.perform(patch("/api/orders/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderStatusUpdate)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
