package com.example.msstore.rest.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoSpacesValidator.class)
public @interface NoSpaces {

    String message() default "O campo não pode conter apenas espaços";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
