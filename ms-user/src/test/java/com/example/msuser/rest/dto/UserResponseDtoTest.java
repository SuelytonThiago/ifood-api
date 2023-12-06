package com.example.msuser.rest.dto;

import com.example.msuser.domain.entities.Users;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

public class UserResponseDtoTest {

    @Test
    public void testUserResponseDtoOFMethod(){
        Users user = new Users();
        user.setId(1L);
        user.setName("ana");
        user.setEmail("ana@example.com");
        user.setContactNumber("99940028922");
        user.setCpf("05661795041");
        user.setPassword("senha123");
        user.setAddresses(new ArrayList<>());
        user.setRoles(new ArrayList<>());

        var userResponse = UserResponseDto.of(user);

        assertThat(userResponse.getId()).isEqualTo(user.getId());
        assertThat(userResponse.getName()).isEqualTo(user.getName());
        assertThat(userResponse.getContactNumber()).isEqualTo(user.getContactNumber());
        assertThat(userResponse.getCpf()).isEqualTo(user.getCpf());
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResponse.getPassword()).isEqualTo(user.getPassword());
    }
}
