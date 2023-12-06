package com.example.msstore.grpc.service.exceptions;

import com.example.msstore.rest.services.exceptions.ObjectNotFoundException;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class ExceptionGrpcHandlerController {

    @GrpcExceptionHandler(ObjectNotFoundException.class)
    public StatusRuntimeException objectNotFound(ObjectNotFoundException e){
        return e.getStatusGrpcCode()
                .withCause(e.getCause())
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
