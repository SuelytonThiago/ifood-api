package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

public class UserRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidUserRequestDto(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@xample.com",
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testEmptyUserName(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "",
                "ana@xample.com",
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("the name cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullUserName(){
        UserRequestDto userRequestDto = new UserRequestDto(
                null,
                "ana@xample.com",
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("the name cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidEmail(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana",
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("Insert a valid email").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyEmail(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "",
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("The email cannot be Empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullEmail(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                null,
                "99940028922",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("The email cannot be Empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullContactNumber(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                null,
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("The contact number cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidContactNumber(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                "40028",
                "05661795041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("Please provide us with a valid telephone number").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidCpf(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                "99940028922",
                "0566179041",
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("Insert a valid cpf").isEqualTo(violations.iterator().next().getMessage());
    }


    @Test
    public void testNullCpf(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                "99940028922",
                null,
                "senha123");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("The cpf cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullPassword(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                "99940028922",
                "05661795041",
                null);

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("The password cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidPassword(){
        UserRequestDto userRequestDto = new UserRequestDto(
                "ana",
                "ana@example.com",
                "99940028922",
                "05661795041",
                "df");

        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(userRequestDto);

        assertThat("the password must contain letters and numbers,and have at least 8 characters").isEqualTo(violations.iterator().next().getMessage());
    }


}
