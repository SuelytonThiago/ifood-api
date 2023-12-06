package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserRequestUpdateTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidUserRequestUpdate(){
        UserRequestUpdate userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("senha123");

        Set<ConstraintViolation<UserRequestUpdate>> violations = validator.validate(userRequestUpdate);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testInvalidEmail(){
        UserRequestUpdate userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("senha123");

        Set<ConstraintViolation<UserRequestUpdate>> violations = validator.validate(userRequestUpdate);

        assertThat("insert a valid email").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidContactNumber(){
        UserRequestUpdate userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("28922");
        userRequestUpdate.setPassword("senha123");

        Set<ConstraintViolation<UserRequestUpdate>> violations = validator.validate(userRequestUpdate);

        assertThat("Please provide us with a valid telephone number").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testInvalidPassword(){
        UserRequestUpdate userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("ana");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("sdf");

        Set<ConstraintViolation<UserRequestUpdate>> violations = validator.validate(userRequestUpdate);

        assertThat("the password must contain letters and numbers,and have at least 8 characters").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testUserNameContainsOnlySpaces(){
        UserRequestUpdate userRequestUpdate  = new UserRequestUpdate();
        userRequestUpdate.setName("  ");
        userRequestUpdate.setEmail("ana@example.com");
        userRequestUpdate.setContactNumber("99940028922");
        userRequestUpdate.setPassword("senha123");

        Set<ConstraintViolation<UserRequestUpdate>> violations = validator.validate(userRequestUpdate);

        assertThat("The name cannot contain only spaces").isEqualTo(violations.iterator().next().getMessage());

    }
}
