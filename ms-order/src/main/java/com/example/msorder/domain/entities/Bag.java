package com.example.msorder.domain.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Bag implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long foodId;

    private String storeName;
    @Min(1)
    private Integer quantity;
    private Double price;
    private Long userId;
    private Instant date;

    public Bag(Long foodId, Integer quantity, Double price, Long userId, String storesInfo) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
        this.userId = userId;
        this.storeName = storesInfo;
        this.date = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bag bag)) return false;
        return getId().equals(bag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
