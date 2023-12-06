package com.example.msstore.rest.dto;

import com.example.msstore.domain.entities.Food;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FoodDto implements Serializable {

    @NotBlank(message = "the food name cannot be empty or null")
    private String name;
    @NotNull(message = "the price cannot be null")
    private Double price;
    @NotNull(message = "the quantity stock cannot be null")
    private Integer quantityStock;
    @NotBlank(message = "the category name cannot be empty or null")
    private String categoryName;

    public FoodDto(Food food){
        name = food.getName();
        price = food.getPrice();
        quantityStock = food.getQuantityStock();
        categoryName =food.getCategory().getName();
    }

}
