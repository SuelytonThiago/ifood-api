package com.example.msstore.rest.dto;

import com.example.msstore.rest.dto.validation.Password;
import com.example.msstore.rest.dto.validation.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OwnerRequestDto implements Serializable {

    @NotBlank(message = "The user name cannot be empty or null")
    private String name;

    @NotNull(message = "The email cannot be null")
    @Email(message = "Insert a valid email")
    private String email;

    @NotNull(message = "The contact number cannot be null")
    @PhoneNumber(message = "Please provide us with a valid telephone number")
    private String contactNumber;

    @NotNull(message = "The cpf cannot be null")
    @CPF(message = "Insert a valid cpf")
    private String cpf;

    @Password(message = "the password must contain letters and numbers,and have at least 8 characters")
    @NotNull(message = "The password cannot be null")
    private String password;
}
