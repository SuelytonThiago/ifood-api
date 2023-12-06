package com.example.msuser.rest.dto.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardNumberValidation implements ConstraintValidator<CardNumber, String> {
    @Override
    public void initialize(CardNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cardNumber, ConstraintValidatorContext constraintValidatorContext) {
        if(cardNumber == null){
            return true;
        }
        return cardNumber.matches("^[0-9]{16}$");
    }
}
