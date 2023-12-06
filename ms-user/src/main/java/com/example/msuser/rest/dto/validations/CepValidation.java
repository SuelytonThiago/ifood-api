package com.example.msuser.rest.dto.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CepValidation implements ConstraintValidator<Cep,String> {
    @Override
    public void initialize(Cep constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext constraintValidatorContext) {
        if (cep == null) {
            return true;
        }

        return cep.matches("^\\d{8}$");
    }
}
