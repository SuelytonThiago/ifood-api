package com.example.msuser.rest.dto.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CardCvvValidation implements ConstraintValidator<CardCvv, String> {
    @Override
    public void initialize(CardCvv constraintAnnotation) {
    }

    @Override
    public boolean isValid(String cvv, ConstraintValidatorContext constraintValidatorContext) {
        if(cvv == null){
            return true;
        }
        return cvv.matches("^\\d{3}$");
    }
}
