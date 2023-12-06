package com.example.msstore.domain.entities;

import com.example.msstore.rest.dto.FoodDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Food implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double price;
    private Integer quantityStock;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "foodRestaurant_id")
    private Store foodStore;

    public Food(FoodDto foodDto, Categories catRequest, Store store){
        name = foodDto.getName();
        price = foodDto.getPrice();
        quantityStock = foodDto.getQuantityStock();
        category = catRequest;
        foodStore = store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Food food)) return false;
        return getId().equals(food.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
