package com.example.msuser.rest.services.exceptions;


import io.grpc.Status;

public class AlreadyExistsException extends RuntimeException{

    public AlreadyExistsException(String message) {
        super(message);
    }

    public Status getGrpcStatusCode(){
        return Status.ALREADY_EXISTS;
    }

}
