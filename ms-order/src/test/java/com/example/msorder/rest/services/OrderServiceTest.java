package com.example.msorder.rest.services;

import com.example.address.AddressResponse;
import com.example.card.CardResponse;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.enums.OrderStatus;
import com.example.msorder.domain.repositories.OrderItemsRepository;
import com.example.msorder.domain.repositories.OrdersRepository;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.rest.dto.AddressDto;
import com.example.msorder.rest.dto.OrderRequestDto;
import com.example.msorder.rest.dto.OrderResponseDto;
import com.example.msorder.rest.dto.OrderStatusUpdate;
import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private OrderItemsRepository orderItemsRepository;
    @Mock
    private MyFoodGrpcClient foodGrpcClient;
    @Mock
    private MyAddressGrpcClient addressGrpcClient;
    @Mock
    private MyCardGrpcClient cardGrpcClient;
    @Mock
    private BagService bagService;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private OrderService orderService;

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
    @DisplayName("test given HttpServletRequest and orderRequestDto object when createNewOrder will save new order in database")
    public void testCreateNewOrder(){
        HttpServletRequest request = mock(HttpServletRequest.class);
        given(tokenService.getClaimId(any(HttpServletRequest.class))).willReturn(bag.getUserId());
        given(bagService.getBagItems(anyLong())).willReturn(Collections.singletonList(bag));
        given(cardGrpcClient.findCardById(anyLong())).willReturn(cardResponse);
        given(addressGrpcClient.findAddressById(anyLong())).willReturn(addressResponse);
        given(ordersRepository.save(any(Orders.class))).willReturn(order);

        orderService.createNewOrder(orderRequestDto,request);

        verify(tokenService).getClaimId(any(HttpServletRequest.class));
        verify(bagService).getBagItems(anyLong());
        verify(cardGrpcClient).findCardById(anyLong());
        verify(addressGrpcClient).findAddressById(anyLong());
        verify(ordersRepository).save(any(Orders.class));
        verify(bagService).emptyBag(any());

        verifyNoMoreInteractions(tokenService);
        verifyNoMoreInteractions(bagService);
        verifyNoMoreInteractions(cardGrpcClient);
        verifyNoMoreInteractions(addressGrpcClient);
        verifyNoMoreInteractions(ordersRepository);
        verifyNoMoreInteractions(bagService);
    }

    @Test
    @DisplayName("test given orderId when findById then return Order object")
    public void testFindById(){
        given(ordersRepository.findById(anyLong())).willReturn(Optional.of(order));

        var orderOutput = orderService.findById(bag.getUserId());

        assertThat(orderOutput).usingRecursiveComparison()
                .isEqualTo(order);

        verify(ordersRepository).findById(anyLong());
        verifyNoMoreInteractions(ordersRepository);
    }

    @Test
    @DisplayName("test given invalid orderId when findById then throw ObjectNotFoundException")
    public void testFindByIdWithInvalidOderId(){
        given(ordersRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> orderService.findById(100L))
                .withMessage("The order is not found");
    }

    @Test
    @DisplayName("test given userId when findAllOrdersByUserId then return OrderResponseDto List")
    public void testFindAllOrdersByUserId(){
        given(ordersRepository.findByUserId(anyLong())).willReturn(Collections.singletonList(order));

        var orderList = orderService.findAllOrdersByUserId(order.getUserId());

        assertThat(orderList)
                .extracting("date","status","storeName","address","paymentMethod","total")
                .contains(
                        tuple(
                                orderResponse.getDate(),
                                orderResponse.getStatus(),
                                orderResponse.getStoreName(),
                                orderResponse.getAddress(),
                                orderResponse.getPaymentMethod(),
                                orderResponse.getTotal()));

        verify(ordersRepository).findByUserId(anyLong());
        verifyNoMoreInteractions(ordersRepository);
    }

    @Test
    @DisplayName("test given orderStatusUpdate object when updateOrderStatus will update OrderStatus")
    public void testUpdateOrderStatus(){
        given(ordersRepository.findById(anyLong())).willReturn(Optional.of(order));

        orderService.updateOrderStatus(orderStatusUpdate);

        verify(ordersRepository).findById(anyLong());
        verify(ordersRepository).save(any(Orders.class));
        verifyNoMoreInteractions(ordersRepository);
    }
}
