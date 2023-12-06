package com.example.msstore.rest.dto;

import com.example.msstore.rest.dto.validation.NoSpaces;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StoreRequestUpdate implements Serializable {

    @NoSpaces(message = "The name cannot contain only spaces")
    private String name;
    @NoSpaces(message = "The bio cannot contain only spaces")
    private String bio;
}
