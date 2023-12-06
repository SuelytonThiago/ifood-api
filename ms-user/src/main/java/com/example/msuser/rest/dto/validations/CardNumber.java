package com.example.msuser.rest.dto.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidation.class)
public @interface CardNumber {

    String message() default "insira um número de cartão válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
