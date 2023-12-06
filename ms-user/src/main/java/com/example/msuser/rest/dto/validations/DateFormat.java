package com.example.msuser.rest.dto.validations;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateFormatterValidation.class)
public @interface DateFormat {
    String message() default "Formato de data inválido. Use o formato MM/yyyy";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
