package com.nebulaparfums.nebula_parfums.exception;

import com.nebulaparfums.nebula_parfums.model.LogActividad;
import com.nebulaparfums.nebula_parfums.model.Usuario;
import com.nebulaparfums.nebula_parfums.service.interfaces.ILogActividadService;
import com.nebulaparfums.nebula_parfums.service.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptions {

    @Autowired
    IUsuarioService usuarioService;
    @Autowired
    ILogActividadService logActividadService;

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
    public ResponseEntity<CustomMessageException> handleException(AccessDeniedException e) {
        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.UNAUTHORIZED.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("No tiene permisos para acceder a esta funcionalidad");
        return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<CustomMessageException> handleException(InvalidPasswordException e) {

        LogActividad logActividad = new LogActividad();

        Usuario usuario = usuarioService.getUsuarioByEmail(e.getMessage());

        if (usuario.getRol().getId_rol() == 1 || usuario.getRol().getId_rol() == 2) {
            logActividad.setUsuario(usuario);
            logActividad.setAccion("Login fallido");
            logActividad.setDetalle("Usuario " + usuario.getNombre() + " realizo un intento login");
            logActividad.setFecha_actualizacion(LocalDateTime.now());
            logActividadService.saveLogActividad(logActividad);
        }

        CustomMessageException exception = new CustomMessageException();
        exception.setStatus(HttpStatus.UNAUTHORIZED.value());
        exception.setTimestamp(System.currentTimeMillis());
        exception.setMessage("La contrasena fallo");
        return new ResponseEntity<>(exception, HttpStatus.UNAUTHORIZED);
    }
}
