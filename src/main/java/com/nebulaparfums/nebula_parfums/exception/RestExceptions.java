package com.nebulaparfums.nebula_parfums.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class RestExceptions {

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(ResourceNotFoundException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.NOT_FOUND.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage(e.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(MethodArgumentTypeMismatchException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.BAD_REQUEST.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("El tipo de dato del id no es valido");
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(QuantityBelowZeroException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.BAD_REQUEST.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage(e.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(ObjectOptimisticLockingFailureException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.CONFLICT.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("El objeto que se solicito modificar no fue encontrado");
        return new ResponseEntity<>(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(HttpMessageNotReadableException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.CONFLICT.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("Se ingreso un tipo de dato invalido");
        return new ResponseEntity<>(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(UsernameNotFoundException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.NOT_FOUND.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage(e.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(BadCredentialsException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.NOT_FOUND.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("La contraseña no es correcta, intentelo de nuevo");
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }
}
