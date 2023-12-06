package com.example.msuser.rest.controllers.exceptions;

import com.example.msuser.rest.services.exceptions.AlreadyExistsException;
import com.example.msuser.rest.services.exceptions.CustomException;
import com.example.msuser.rest.services.exceptions.NotAuthorizeException;
import com.example.msuser.rest.services.exceptions.ObjectNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ProblemDetail objectNotFound(ObjectNotFoundException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperty("TimeStamp", LocalDate.now());
        problemDetail.setProperty("Message",e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(NotAuthorizeException.class)
    public ProblemDetail notAuthorize(NotAuthorizeException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setProperty("TimeStamp", LocalDate.now());
        problemDetail.setProperty("Message",e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ProblemDetail AlreadyExists(AlreadyExistsException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setProperty("TimeStamp", LocalDate.now());
        problemDetail.setProperty("Message",e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(CustomException.class)
    public ProblemDetail Custom(CustomException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("TimeStamp", LocalDate.now());
        problemDetail.setProperty("Message",e.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail methodArgumentNotValid(MethodArgumentNotValidException e){
        List<String> err =e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("TimeStamp",LocalDate.now());
        problemDetail.setProperty("Message",err);
        return problemDetail;
    }
}
