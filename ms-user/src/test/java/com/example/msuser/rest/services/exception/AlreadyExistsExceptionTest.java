package com.example.msuser.rest.services.exception;

import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlreadyExistsExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Entity already exists";
        AlreadyExistsException exception = new AlreadyExistsException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGrpcStatusCode() {
        AlreadyExistsException exception = new AlreadyExistsException("Entity already exists");
        Status grpcStatus = exception.getGrpcStatusCode();

        assertEquals(Status.ALREADY_EXISTS, grpcStatus);
    }
}
