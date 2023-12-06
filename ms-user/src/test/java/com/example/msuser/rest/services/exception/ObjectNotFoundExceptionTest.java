package com.example.msuser.rest.services.exception;

import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import io.grpc.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectNotFoundExceptionTest {

    @Test
    public void testExceptionMessage() {
        String message = "Object not found";
        ObjectNotFoundException exception = new ObjectNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testGrpcStatusCode() {
        ObjectNotFoundException exception = new ObjectNotFoundException("Object not found");
        Status grpcStatus = exception.getGrpcStatusCode();

        assertEquals(Status.NOT_FOUND, grpcStatus);
    }
}
