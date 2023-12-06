package com.example.msorder.rest.dto;
import com.example.msorder.domain.entities.OrderItems;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItemsResponseDto implements Serializable {

    private String foodName;
    private Integer quantity;
    private Double price;
    private String subTotal;

    public OrderItemsResponseDto(OrderItems orderItems, String nameFood,String subTotalInf){
        foodName = nameFood;
        quantity = orderItems.getQuantity();
        price = orderItems.getPrice();
        subTotal = subTotalInf;
    }
}
