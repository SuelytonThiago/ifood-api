package com.example.msuser.domain.entities;

import com.example.msuser.domain.enums.TypeCard;
import com.example.msuser.rest.dto.CardRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Cards implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String number;
    private String expiration;
    private String cvv;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users cardUser;
    private TypeCard type;

    public Cards(CardRequestDto dto, Users users, TypeCard typeCard){
        name = dto.getName();
        number = dto.getNumber();
        expiration = dto.getExpiration();
        cvv = dto.getCvv();
        cardUser = users;
        type = typeCard;
    }


    public Cards(String name, String number, String expiration, String cvv, Users cardUser, TypeCard type) {
        this.name = name;
        this.number = number;
        this.expiration = expiration;
        this.cvv = cvv;
        this.cardUser = cardUser;
        this.type = type;
    }
}
