package com.example.msorder.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderStatusUpdate implements Serializable {

    @NotNull(message = "the status code cannot be null")
    private int statusCod;
    @NotNull(message = "the order id cannot be null")
    private Long orderId;
}
