package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.Set;

public class CardRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidCardRequestDto(){
        CardRequestDto cardRequestDto = new CardRequestDto("myCard","1234567891234567","12/2024","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testInvalidCardNumber(){
        CardRequestDto cardRequestDto = new CardRequestDto("myCard","123456734567","12/2024","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("Enter a valid card number").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCardNumber(){
        CardRequestDto cardRequestDto = new CardRequestDto("myCard",null,"12/2024","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("The card number cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCardName(){
        CardRequestDto cardRequestDto = new CardRequestDto(null,"1234567891234567","12/2024","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("The card name cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyCardName(){
        CardRequestDto cardRequestDto = new CardRequestDto("","1234567891234567","12/2024","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("The card name cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCardExpiration(){
        CardRequestDto cardRequestDto = new CardRequestDto("MyCard","1234567891234567","12/20244521","266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("Invalid date format. Use the format MM/yyyy").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCardExpiration(){
        CardRequestDto cardRequestDto = new CardRequestDto("MyCard","1234567891234567",null,"266",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("The card expiration cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCardCvv(){
        CardRequestDto cardRequestDto = new CardRequestDto("MyCard","1234567891234567","12/2024","266adas",1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("Enter a valid security code").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCardCvv(){
        CardRequestDto cardRequestDto = new CardRequestDto("MyCard","1234567891234567","12/2024",null,1);
        Set<ConstraintViolation<CardRequestDto>> violations = validator.validate(cardRequestDto);

        assertThat("The card cvv cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }
}
