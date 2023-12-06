package com.example.msuser.domain.entities;

import com.example.msuser.rest.dto.AddressRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest {

    private AddressRequestDto addressRequestDto;

    private Users user;

    @BeforeEach
    void setUp(){
        addressRequestDto = new AddressRequestDto();
        addressRequestDto.setCep("00000000");
        addressRequestDto.setStreet("rua a");
        addressRequestDto.setCity("cidade a");
        addressRequestDto.setState("estado a");
        addressRequestDto.setNeighborhood("bairro a");

        user = new Users("james",
                "james@example.com",
                "40028922",
                "88688901007",
                "senha123");
    }

    @Test
    public void testGivenAddressRequestDto_whenOf_thenReturnAddressObject(){
        var address = Address.of(addressRequestDto, user);

        assertThat(address).usingRecursiveComparison()
                .comparingOnlyFields("cep","street","state","city","neighborhood")
                .isEqualTo(addressRequestDto);
    }

}
