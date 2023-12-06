package com.example.msorder.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderResponseDto implements Serializable {

    private String date;
    private String status;
    private String storeName;
    private AddressDto address;
    private List<OrderItemsResponseDto> items = new ArrayList<>();
    private String paymentMethod;
    private String total;

}
