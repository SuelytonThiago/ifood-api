package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserLoginDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidUserLoginDto(){
        UserLoginDto userLoginDto = new UserLoginDto(
                "ana@example.com",
                "senha123");

        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testEmptyUserEmail(){
        UserLoginDto userLoginDto = new UserLoginDto(
                "",
                "senha123");

        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

        assertThat("The user email cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullUserEmail(){
        UserLoginDto userLoginDto = new UserLoginDto(
                null,
                "senha123");

        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

        assertThat("The user email cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyUserPassword(){
        UserLoginDto userLoginDto = new UserLoginDto(
                "ana@example.com",
                "");

        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

        assertThat("The password cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullUserPassword(){
        UserLoginDto userLoginDto = new UserLoginDto(
                "ana@example.com",
                null);

        Set<ConstraintViolation<UserLoginDto>> violations = validator.validate(userLoginDto);

        assertThat("The password cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }
}
