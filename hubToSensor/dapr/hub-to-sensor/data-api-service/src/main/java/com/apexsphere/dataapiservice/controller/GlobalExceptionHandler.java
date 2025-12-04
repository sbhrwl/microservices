package com.apexsphere.dataapiservice.controller;

import com.apexsphere.dataapiservice.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        // Returns HTTP 404 Not Found and the exception message as the response body
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}