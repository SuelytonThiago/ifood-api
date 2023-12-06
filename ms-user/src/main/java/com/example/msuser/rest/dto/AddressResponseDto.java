package com.example.msuser.rest.dto;

import com.example.msuser.domain.entities.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto implements Serializable {

    private Long id;
    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String cep;

    public static AddressResponseDto of(Address address){
        AddressResponseDto responseDto = new AddressResponseDto();
        responseDto.setId(address.getId());
        responseDto.setStreet(address.getStreet());
        responseDto.setNeighborhood(address.getNeighborhood());
        responseDto.setCity(address.getCity());
        responseDto.setCep(address.getCep());
        responseDto.setState(address.getState());

        return responseDto;
    }

}
