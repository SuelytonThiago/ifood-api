package com.example.msuser.rest.services.exception;

import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
import com.example.msuser.rest.services.exceptions.CustomException;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Error";
        CustomException exception = new CustomException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGrpcStatusCode() {
        CustomException exception = new CustomException("Error");
        Status grpcStatus = exception.getGrpcStatusCode();

        assertEquals(Status.INVALID_ARGUMENT, grpcStatus);
    }
}
