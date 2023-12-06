package com.example.msstore.rest.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpacesValidator implements ConstraintValidator<NoSpaces,String> {
    @Override
    public void initialize(NoSpaces constraintAnnotation) {
    }

    @Override
    public boolean isValid(String field, ConstraintValidatorContext constraintValidatorContext) {
        if (field == null) {
            return true;
        }

        return field.matches(".*[a-zA-Z].*");
    }
}
