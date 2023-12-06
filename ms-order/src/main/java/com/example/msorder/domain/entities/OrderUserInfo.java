package com.example.msorder.domain.entities;

import com.example.address.AddressResponse;
import com.example.card.CardResponse;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class OrderUserInfo {

    private String street;
    private String neighborhood;
    private String city;
    private String state;
    private String cep;
    private String paymentMethod;

    public OrderUserInfo(AddressResponse address, CardResponse card){
        street = address.getStreet();
        neighborhood = address.getNeighborhood();
        city = address.getCity();
        state = address.getState();
        cep = address.getCep();
        paymentMethod = card.getTypeCard();
    }

}
