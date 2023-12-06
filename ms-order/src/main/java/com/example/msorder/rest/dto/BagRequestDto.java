package com.example.msorder.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BagRequestDto implements Serializable {

    @NotNull(message = "the food id cannot be empty or null")
    private Long foodId;
    @NotNull(message = "the quantity cannot be empty or null")
    private Integer quantity;

}
