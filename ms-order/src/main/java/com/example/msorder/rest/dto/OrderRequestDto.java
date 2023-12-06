package com.example.msorder.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequestDto implements Serializable {

    @NotNull(message = "the addressId cannot be null")
    private Long addressId;

    @NotNull(message = "the paymentMethod cannot be null")
    private Long paymentMethodId;
}
