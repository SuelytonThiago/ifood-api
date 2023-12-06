package com.example.msuser.rest.services.exception;

import com.example.msuser.rest.services.exceptions.NotAuthorizeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotAuthorizeExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Not authorized";
        NotAuthorizeException exception = new NotAuthorizeException(message);

        assertEquals(message, exception.getMessage());
    }
}
