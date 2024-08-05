package com.alifetvaci.credit.userservice.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handleException (GenericException ex){
        Map<String, Object> errors = new HashMap<>();
        errors.put("success", false);
        errors.put("message", ex.getErrorCode());
        log.error(ex.getLogMessage());
        return ResponseEntity
                .status(ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleException (Throwable ex){
        log.error("GlobalExceptionHandler -> handleException is started, ex: {}", ex.getMessage());
        Map<String, Object> errors = new HashMap<>();
        errors.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errors);
    }
}
