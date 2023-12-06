package com.example.msorder.domain.repositories;

import com.example.msorder.config.ContainerBase;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.enums.OrderStatus;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.grpc.client.MyUserGrpcClient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private OrdersRepository ordersRepository;

    @MockBean
    private MyAddressGrpcClient myAddressGrpcClient;
    @MockBean
    private MyCardGrpcClient myCardGrpcClient;
    @MockBean
    private MyFoodGrpcClient myFoodGrpcClient;
    @MockBean
    private MyUserGrpcClient myUserGrpcClient;

    private static Orders order;

    @Test
    @Order(1)
    @DisplayName("test given Order object when save then rturn order object")
    public void integrationTestSave(){
        OrderUserInfo orderUserInfo = new OrderUserInfo(
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                "CREDIT_CARD");
        Orders inputOrder = new Orders(null,
                LocalDate.now(),
                15L,
                "habbibs",
                orderUserInfo,
                OrderStatus.WAITING_CONFIRMATION);

        var savedOrder = ordersRepository.save(inputOrder);

        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder)
                .usingRecursiveComparison()
                .comparingOnlyFields("userId","storeName","orderUserInfo","orderStatus")
                .isEqualTo(inputOrder);

        order = savedOrder;
    }

    @Test
    @Order(2)
    @DisplayName("Test given orderId when findById then return orderObject")
    public void integrationTestFindById(){
        var outputOrder = ordersRepository.findById(order.getId()).get();

        assertThat(outputOrder).isNotNull();
        assertThat(outputOrder)
                .usingRecursiveComparison()
                .comparingOnlyFields("userId","storeName","orderUserInfo","orderStatus")
                .isEqualTo(order);
    }

    @Test
    @Order(3)
    @DisplayName("Test given userId when findByUserId then return OrderList")
    public void integrationTetFindByUserId(){
        var listOrder = ordersRepository.findByUserId(order.getUserId());

        assertThat(listOrder)
                .extracting("userId","storeName","orderUserInfo","orderStatus")
                .contains(
                        tuple(order.getUserId(),order.getStoreName(),order.getOrderUserInfo(),order.getOrderStatus())
                );
    }

    @Test
    @Order(4)
    @Transactional
    @DisplayName("Test given orderId when delete will delete this order from database")
    public void integrationTestDelete(){
        ordersRepository.delete(order);
        assertThat(ordersRepository.findById(order.getId()).isEmpty()).isTrue();
    }
}
