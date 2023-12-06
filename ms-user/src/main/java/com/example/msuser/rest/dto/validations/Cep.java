package com.example.msuser.rest.dto.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Constraint(validatedBy = CepValidation.class)
public @interface Cep {


    String message() default "insira um cep válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
