package com.pims.exception;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(ApiException.class)
public ResponseEntity<Map<String, String>> handleApiException(ApiException ex) {

    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}

}
