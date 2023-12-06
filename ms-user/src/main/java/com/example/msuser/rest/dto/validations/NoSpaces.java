package com.example.msuser.rest.dto.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;


import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoSpacesValidator.class)
public @interface NoSpaces {

    String message() default "O campo não pode conter apenas espaços";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
