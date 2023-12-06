package com.example.msorder.rest.dto;

import com.example.address.AddressResponse;
import com.example.msorder.domain.entities.OrderUserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto implements Serializable {
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String cep;

    public AddressDto(OrderUserInfo orderUserInfo){
        street = orderUserInfo.getStreet();
        neighborhood = orderUserInfo.getNeighborhood();
        city = orderUserInfo.getCity();
        state = orderUserInfo.getState();
        cep = orderUserInfo.getCep();
    }
}
