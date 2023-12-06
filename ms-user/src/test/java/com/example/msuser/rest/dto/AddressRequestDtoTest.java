package com.example.msuser.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import java.util.Set;

public class AddressRequestDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    public void testValidAddressRequestDto(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    public void testInvalidCep(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "cidade a", "estado a", "invalid");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("Insert a valid cep").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCep(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "cidade a", "estado a", null);
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The cep cannot be null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyState(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "cidade a", "", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The state cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullState(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "cidade a", null, "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The state cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyCity(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", "", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The city cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullCity(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "bairro a", null, "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The city cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyNeighborhood(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", "", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The neighborhood cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullNeighborhood(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("rua a", null, "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The neighborhood cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testEmptyStreet(){
        AddressRequestDto addressRequestDto = new AddressRequestDto("", "bairro a", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The street cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }

    @Test
    public void testNullStreet(){
        AddressRequestDto addressRequestDto = new AddressRequestDto(null, "bairro a", "cidade a", "estado a", "12345678");
        Set<ConstraintViolation<AddressRequestDto>> violations = validator.validate(addressRequestDto);

        assertThat("The street cannot be empty or null").isEqualTo(violations.iterator().next().getMessage());
    }
}
