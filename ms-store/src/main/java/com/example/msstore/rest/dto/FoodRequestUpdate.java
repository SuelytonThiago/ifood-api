package com.example.msstore.rest.dto;

import com.example.msstore.rest.dto.validation.NoSpaces;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequestUpdate implements Serializable {

    @NoSpaces(message = "the food name cannot be empty")
    private String name;
    private Double price;
    private Integer quantityStock;
    @NoSpaces(message = "the category name cannot be empty")
    private String categoryName;
}
