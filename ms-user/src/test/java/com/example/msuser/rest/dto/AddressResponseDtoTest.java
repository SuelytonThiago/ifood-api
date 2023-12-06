package com.example.msuser.rest.dto;

import com.example.msuser.domain.entities.Address;
import com.example.msuser.domain.entities.Users;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;


public class AddressResponseDtoTest {

    @Test
    public void testAddressResponseDtoOFMethod() {
        Users user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("40028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        Address address = new Address(
                1L,
                "rua a",
                "bairro a",
                "cidade a",
                "estado a",
                "00000000",
                user);

        var addressResponse = AddressResponseDto.of(address);

        assertThat(addressResponse.getId()).isEqualTo(address.getId());
        assertThat(addressResponse.getCep()).isEqualTo(address.getCep());
        assertThat(addressResponse.getCity()).isEqualTo(address.getCity());
        assertThat(addressResponse.getState()).isEqualTo(address.getState());
        assertThat(addressResponse.getStreet()).isEqualTo(address.getStreet());
        assertThat(addressResponse.getNeighborhood()).isEqualTo(address.getNeighborhood());

    }
}
