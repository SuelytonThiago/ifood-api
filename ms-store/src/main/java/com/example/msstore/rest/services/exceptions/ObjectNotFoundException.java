package com.example.msstore.rest.services.exceptions;

import io.grpc.Status;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String message) {
        super(message);
    }
    public Status getStatusGrpcCode(){
        return Status.NOT_FOUND;
    }
}
