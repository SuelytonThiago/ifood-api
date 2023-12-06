package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CardRequestUpdateTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testNameContainOnlySpaces(){
       CardRequestUpdate cardRequestUpdate = new CardRequestUpdate(
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266",
                1);

        Set<ConstraintViolation<CardRequestUpdate>> violations = validator.validate(cardRequestUpdate);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testInvalidCardNumber(){
        CardRequestUpdate cardRequestUpdate = new CardRequestUpdate(
                "MyCARD",
                "1234567asd891234567",
                "12/2028",
                "266",
                1);

        Set<ConstraintViolation<CardRequestUpdate>> violations = validator.validate(cardRequestUpdate);

        assertThat("Enter a valid card number").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCardExpiration(){
        CardRequestUpdate cardRequestUpdate = new CardRequestUpdate(
                "MyCARD",
                "1234567891234567",
                "12/202856",
                "266",
                1);

        Set<ConstraintViolation<CardRequestUpdate>> violations = validator.validate(cardRequestUpdate);

        assertThat("Invalid date format. Use the format MM/yyyy").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCardCvv(){
        CardRequestUpdate cardRequestUpdate = new CardRequestUpdate(
                "MyCARD",
                "1234567891234567",
                "12/2028",
                "266asd",
                1);

        Set<ConstraintViolation<CardRequestUpdate>> violations = validator.validate(cardRequestUpdate);

        assertThat("insert a valid security card code").isEqualTo(violations.iterator().next().getMessage());
    }


}
