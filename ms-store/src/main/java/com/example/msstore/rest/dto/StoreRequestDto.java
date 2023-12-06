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
public class StoreRequestDto implements Serializable {

    @NotBlank(message = "the name cannot be empty or null")
    private String storeName;

    @NotBlank(message = "the bio cannot be empty or null")
    private String bio;

    @NotNull(message = "the type code cannot be null")
    private int typeCode;

    //ownerProfile
    @NotBlank(message = "The user name cannot be empty or null")
    private String ownerName;

    @NotBlank(message = "The email cannot be Empty or null")
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
