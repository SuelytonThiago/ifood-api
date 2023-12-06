package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class AddressRequestUpdateTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void testValidAddressRequestDto(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("rua a", "bairro a", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testInvalidCep(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("rua a", "bairro a", "cidade a", "estado a", "invalid");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat("Insert a valid cep").isEqualTo(violations.iterator().next().getMessage());
    }


    @Test
    public void testEmptyState(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("rua a", "bairro a", "cidade a", " ", "12345678");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat("The state cannot contain only spaces").isEqualTo(violations.iterator().next().getMessage());
    }


    @Test
    public void testEmptyCity(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("rua a", "bairro a", " ", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat("The city cannot contain only spaces").isEqualTo(violations.iterator().next().getMessage());
    }



    @Test
    public void testEmptyNeighborhood(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("rua a", " ", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat("The neighborhood cannot contain only spaces").isEqualTo(violations.iterator().next().getMessage());
    }


    @Test
    public void testEmptyStreet(){
        AddressRequestUpdate addressRequestUpdate = new AddressRequestUpdate("  ", "bairro a", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestUpdate>> violations = validator.validate(addressRequestUpdate);

        assertThat("The street cannot contain only spaces").isEqualTo(violations.iterator().next().getMessage());
    }

}
