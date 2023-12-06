package com.example.msorder.domain.repositories;

import com.example.msorder.config.ContainerBase;
import com.example.msorder.domain.entities.OrderItems;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderItemsRepositoryIntegrationTest extends ContainerBase {

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @MockBean
    private MyAddressGrpcClient myAddressGrpcClient;
    @MockBean
    private MyCardGrpcClient myCardGrpcClient;
    @MockBean
    private MyFoodGrpcClient myFoodGrpcClient;
    @MockBean
    private MyUserGrpcClient myUserGrpcClient;

    private static OrderItems orderItem;


    @Test
    @Order(1)
    @DisplayName("test given orderitem object when save then return OrdrItem object")
    public void integrationTestSave(){
        OrderItems inputOrderItems = new OrderItems(
                null,
                1L,
                1,
                15.00,
                null);

        var savedOrderItem = orderItemsRepository.save(inputOrderItems);

        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem)
                .usingRecursiveComparison()
                .comparingOnlyFields("foodId","quantity","price")
                .isEqualTo(inputOrderItems);

        orderItem = savedOrderItem;
    }

    @Test
    @Order(2)
    @DisplayName("test given orderItemId whem findById then return orderIdObject")
    public void integrationTestFindById(){
        var outputOrderItem = orderItemsRepository.findById(orderItem.getId());

        assertThat(outputOrderItem)
                .usingRecursiveComparison()
                .comparingOnlyFields("foodId","quantity","price")
                .isEqualTo(orderItem);
    }

    @Test
    @Order(3)
    @Transactional
    @DisplayName("test given orderItemId when delete will delete orderItem from database")
    public void integrationTestDelete(){
        orderItemsRepository.delete(orderItem);

        assertThat(orderItemsRepository.findById(orderItem.getId()).isEmpty()).isTrue();
    }



}
