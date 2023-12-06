package com.example.msuser.rest.services.exceptions;

import io.grpc.Status;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String message) {
        super(message);
    }


    public Status getGrpcStatusCode(){
        return Status.NOT_FOUND;
    }

}
