package com.example.msuser.rest.dto;

import com.example.msuser.rest.dto.validations.CardCvv;
import com.example.msuser.rest.dto.validations.CardNumber;
import com.example.msuser.rest.dto.validations.DateFormat;
import com.example.msuser.rest.dto.validations.NoSpaces;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CardRequestUpdate implements Serializable {

    @NoSpaces(message = "The card name cannot contain only spaces")
    private String name;
    @CardNumber(message = "Enter a valid card number")
    private String number;
    @DateFormat(message = "Invalid date format. Use the format MM/yyyy")
    private String expiration;
    @CardCvv(message = "insert a valid security card code")
    private String cvv;
    private int typeCod;
}
