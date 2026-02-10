package com.openclassrooms.mddapi.exception;

import com.openclassrooms.mddapi.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex,
                                                                  HttpServletRequest request) {
        ApiError error = new ApiError(
                ex.getStatusCode()
                  .value(),
                ex.getReason(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}
