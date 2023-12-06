package com.example.msorder.rest.dto;

import com.example.msorder.domain.entities.Bag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BagResponseDto implements Serializable {

    private Long foodId;
    private Integer quantity;
    private Double price;

    public BagResponseDto(Bag bag){
        foodId = bag.getFoodId();
        quantity = bag.getQuantity();
        price = bag.getPrice();
    }
}
