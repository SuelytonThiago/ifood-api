package com.example.msuser.grpc.exceptions;

import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@GrpcAdvice
public class ExceptionHandlerControllerGrpc {

    @GrpcExceptionHandler(ObjectNotFoundException.class)
    public StatusRuntimeException objectNotFound(ObjectNotFoundException e){
        return e.getGrpcStatusCode()
                .withCause(e.getCause())
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(AlreadyExistsException.class)
    public StatusRuntimeException alreadyExist(AlreadyExistsException e){
        return e.getGrpcStatusCode()
                .withCause(e.getCause())
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(CustomException.class)
    public StatusRuntimeException custom(CustomException e){
        return e.getGrpcStatusCode()
                .withCause(e.getCause())
                .withDescription(e.getMessage())
                .asRuntimeException();
    }
}
