package com.example.msuser.rest.dto;
import com.example.msuser.rest.dto.validations.CardCvv;
import com.example.msuser.rest.dto.validations.CardNumber;
import com.example.msuser.rest.dto.validations.DateFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardRequestDto implements Serializable {

    @NotBlank(message = "The card name cannot be empty or null")
    private String name;

    @NotNull(message = "The card number cannot be null")
    @CardNumber(message = "Enter a valid card number")
    private String number;

    @NotNull(message = "The card expiration cannot be null")
    @DateFormat(message = "Invalid date format. Use the format MM/yyyy")
    private String expiration;

    @NotNull(message = "The card cvv cannot be null")
    @CardCvv(message = "Enter a valid security code")
    private String cvv;

    @NotNull(message = "The type Card cannot be null")
    private int typeCard;
}
