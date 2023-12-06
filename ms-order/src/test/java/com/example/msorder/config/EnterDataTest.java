package com.example.msorder.config;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.entities.OrderItems;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.enums.OrderStatus;
import com.example.msorder.domain.repositories.BagRepository;
import com.example.msorder.domain.repositories.OrderItemsRepository;
import com.example.msorder.domain.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;

@Configuration
@ActiveProfiles("test")
public class EnterDataTest implements CommandLineRunner{

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private BagRepository bagRepository;


    @Override
    public void run(String... args) throws Exception {
        Bag bag = new Bag(
                null,
                1L,
                "habbibs",
                2,
                30.00,
                5L,
                Instant.now());
        bagRepository.save(bag);

        OrderUserInfo orderUserInfo = new OrderUserInfo(
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                "CREDIT_CARD");

        Orders order = new Orders(null,
                LocalDate.now(),
                bag.getUserId(),
                bag.getStoreName(),
                orderUserInfo,
                OrderStatus.WAITING_CONFIRMATION);

        ordersRepository.save(order);

        OrderItems orderItems = new OrderItems(
                null,
                1L,
                1,
                15.00,
                order);
        orderItemsRepository.save(orderItems);

        order.getItems().add(orderItems);
        ordersRepository.save(order);
    }
}
