package com.example.msuser.rest.services.exceptions;

import io.grpc.Status;

public class CustomException extends RuntimeException{

    public CustomException(String message) {
        super(message);
    }

    public Status getGrpcStatusCode(){
        return Status.INVALID_ARGUMENT;
    }

}
