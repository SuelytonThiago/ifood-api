package com.example.msuser.rest.services.exceptions;

public class NotAuthorizeException extends RuntimeException{

    public NotAuthorizeException(String message) {
        super(message);
    }
}
