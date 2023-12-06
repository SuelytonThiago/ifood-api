package com.example.msuser.rest.dto;

import com.example.msuser.domain.entities.Cards;
import com.example.msuser.domain.entities.Users;
import com.example.msuser.domain.enums.TypeCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;


public class CardResponseDtoTest {

    @Test
    public void testCardResponseDtoConstructor(){
        Users user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("40028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        Cards card = new Cards(
                1L,
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                user,
                TypeCard.CREDIT_CARD);

        var cardResponse = new CardResponseDto(card);

        assertThat(cardResponse.getId()).isEqualTo(card.getId());
        assertThat(cardResponse.getName()).isEqualTo(card.getName());
        assertThat(cardResponse.getNumber()).isEqualTo(card.getNumber());
        assertThat(cardResponse.getExpiration()).isEqualTo(card.getExpiration());
        assertThat(cardResponse.getCvv()).isEqualTo(card.getCvv());
        assertThat(cardResponse.getType()).isEqualTo(card.getType().toString());

    }
}
