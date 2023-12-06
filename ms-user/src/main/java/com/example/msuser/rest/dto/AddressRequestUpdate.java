package com.example.msuser.rest.dto;

import com.example.msuser.rest.dto.validations.Cep;
import com.example.msuser.rest.dto.validations.NoSpaces;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestUpdate implements Serializable {

    @NoSpaces(message = "The street cannot contain only spaces")
    private String street;

    @NoSpaces(message = "The neighborhood cannot contain only spaces")
    private String neighborhood;

    @NoSpaces(message = "The city cannot contain only spaces")
    private String city;

    @NoSpaces(message = "The state cannot contain only spaces")
    private String state;

    @Cep(message = "Insert a valid cep")
    private String cep;

}
