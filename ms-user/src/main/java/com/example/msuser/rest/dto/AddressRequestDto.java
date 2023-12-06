package com.example.msuser.rest.dto;

import com.example.msuser.rest.dto.validations.Cep;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto implements Serializable {

    @NotBlank(message = "The street cannot be empty or null")
    private String street;

    @NotBlank(message = "The neighborhood cannot be empty or null")
    private String neighborhood;

    @NotBlank(message = "The city cannot be empty or null")
    private String city;

    @NotBlank(message = "The state cannot be empty or null")
    private String state;

    @Cep(message = "Insert a valid cep")
    @NotNull(message = "The cep cannot be null")
    private String cep;

}
