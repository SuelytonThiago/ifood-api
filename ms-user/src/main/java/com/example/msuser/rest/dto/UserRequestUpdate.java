package com.example.msuser.rest.dto;

import com.example.msuser.rest.dto.validations.NoSpaces;
import com.example.msuser.rest.dto.validations.Password;
import com.example.msuser.rest.dto.validations.PhoneNumber;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUpdate implements Serializable {

    @NoSpaces(message = "The name cannot contain only spaces")
    private String name;

    @Email(message = "insert a valid email")
    private String email;

    @PhoneNumber(message = "Please provide us with a valid telephone number")
    private String contactNumber;

    @Password(message = "the password must contain letters and numbers,and have at least 8 characters")
    private String password;
}
