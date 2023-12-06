package com.example.msuser.rest.dto;

import com.example.msuser.rest.dto.validations.Password;
import com.example.msuser.rest.dto.validations.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto implements Serializable {

    @NotBlank(message = "the name cannot be empty or null")
    private String name;

    @NotBlank(message = "The email cannot be Empty or null")
    @Email(message = "Insert a valid email")
    private String email;

    @NotNull(message = "The contact number cannot be null")
    @PhoneNumber(message = "Please provide us with a valid telephone number")
    private String contactNumber;

    @NotBlank(message = "The cpf cannot be empty or null")
    @CPF(message = "Insert a valid cpf")
    private String cpf;

    @Password(message = "the password must contain letters and numbers,and have at least 8 characters")
    @NotNull(message = "The password cannot be null")
    private String password;
}
