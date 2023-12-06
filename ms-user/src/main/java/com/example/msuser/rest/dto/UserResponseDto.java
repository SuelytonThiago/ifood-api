package com.example.msuser.rest.dto;


import com.example.msuser.domain.entities.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto implements Serializable {

    private Long id;
    private String name;
    private String email;
    private String contactNumber;
    private String cpf;
    private String password;

    public static UserResponseDto of(Users users){
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(users.getId());
        responseDto.setName(users.getName());
        responseDto.setEmail(users.getEmail());
        responseDto.setContactNumber(users.getContactNumber());
        responseDto.setCpf(users.getCpf());
        responseDto.setPassword(users.getPassword());

        return responseDto;
    }

}
