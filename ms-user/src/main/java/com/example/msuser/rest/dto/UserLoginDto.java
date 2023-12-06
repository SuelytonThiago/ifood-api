package com.example.msuser.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto implements Serializable {

    @NotBlank(message = "The user email cannot be empty or null")
    private String email;
    @NotBlank(message = "The password cannot be empty or null")
    private String password;
}
