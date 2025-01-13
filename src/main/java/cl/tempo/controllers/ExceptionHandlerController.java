package cl.tempo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerController {


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMMessageNotReadableException(HttpMessageNotReadableException exception)
    {
        String messageException = exception.getCause().getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception)
    {
        List<ObjectError> errorResponse = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(
                (error)-> errorResponse.add(error)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
