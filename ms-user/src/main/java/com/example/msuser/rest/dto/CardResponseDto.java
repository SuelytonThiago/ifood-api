package com.example.msuser.rest.dto;

import com.example.msuser.domain.entities.Cards;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardResponseDto implements Serializable {

    private Long id;
    private String name;
    private String number;
    private String cvv;
    private String expiration;
    private String type;
    public CardResponseDto(Cards cards){
        id = cards.getId();
        name = cards.getName();
        number = cards.getNumber();
        cvv = cards.getCvv();
        expiration = cards.getExpiration();
        type = cards.getType().toString();
    }

}
