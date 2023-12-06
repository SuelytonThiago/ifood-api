package com.example.msorder.rest.services;
import com.example.msorder.domain.entities.Bag;
import com.example.msorder.domain.entities.OrderItems;
import com.example.msorder.domain.entities.OrderUserInfo;
import com.example.msorder.domain.entities.Orders;
import com.example.msorder.domain.enums.OrderStatus;
import com.example.msorder.grpc.client.MyAddressGrpcClient;
import com.example.msorder.grpc.client.MyCardGrpcClient;
import com.example.msorder.grpc.client.MyFoodGrpcClient;
import com.example.msorder.domain.repositories.OrderItemsRepository;
import com.example.msorder.domain.repositories.OrdersRepository;
import com.example.msorder.rest.dto.*;
import com.example.msorder.rest.services.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;

    private final OrderItemsRepository orderItemsRepository;

    private final MyFoodGrpcClient foodGrpcClient;

    private final MyAddressGrpcClient addressGrpcClient;

    private final MyCardGrpcClient cardGrpcClient;

    private final BagService bagService;

    private final TokenService tokenService;

    @Transactional
    public void createNewOrder(OrderRequestDto orderRequestDto,
                               HttpServletRequest request){
        var userId = tokenService.getClaimId(request);
        var bagItems = bagService.getBagItems(userId);
        var card = cardGrpcClient.findCardById(orderRequestDto.getPaymentMethodId());
        var address = addressGrpcClient.findAddressById(orderRequestDto.getAddressId());
        var storeName = bagItems.get(0).getStoreName();
        var order = ordersRepository.save(
                new Orders(new OrderUserInfo(address,card), userId, storeName));
        order.getItems().addAll(saveAllBagItems(bagItems,order));
        bagService.emptyBag(bagItems);
    }

    public Orders findById(Long id){
        return ordersRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("The order is not found"));
    }

    @Transactional
    public List<OrderResponseDto> findAllOrdersByUserId(Long userId){
        return ordersRepository.findByUserId(userId).stream()
                .map(order ->{
                    OrderResponseDto response = new OrderResponseDto();
                    response.setAddress(new AddressDto( order.getOrderUserInfo()));
                    response.setDate(LocalDate.now().toString());
                    response.setStatus(order.getOrderStatus().toString());
                    response.setStoreName(order.getStoreName());
                    response.setItems(convertOrderItemsList(order.getItems()));
                    response.setPaymentMethod(order.getOrderUserInfo().getPaymentMethod());
                    response.setTotal(String.format("%.2f",order.getTotal()));
                    return response;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderStatus(OrderStatusUpdate update){
        var order = findById(update.getOrderId());
        order.setOrderStatus(OrderStatus.codOf(update.getStatusCod()));
        ordersRepository.save(order);
    }

    private List<OrderItemsResponseDto>convertOrderItemsList(List<OrderItems> list){
        return list.stream().map(item -> {
            var foodName = foodGrpcClient.findById(item.getFoodId()).getName();
            var subTotal = String.format("%.2f",item.getSubTotal());
            return new OrderItemsResponseDto(item,foodName,subTotal);
        }).collect(Collectors.toList());
    }

    private List<OrderItems> saveAllBagItems(List<Bag> items, Orders order){
        return items.stream()
                .map(item -> {
                    return orderItemsRepository.save(new OrderItems(
                            item.getFoodId(),
                            item.getQuantity(),
                            item.getPrice(),
                            order));
                }).collect(Collectors.toList());
    }

    }
