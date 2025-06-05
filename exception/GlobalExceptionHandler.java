package com.p2p.transport.exception;

import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex.getMessage().contains("Invalid JWT")) {
            status = HttpStatus.UNAUTHORIZED;
        }
        ApiResponse<Object> response = new ApiResponse<>(
                status.value(),
                "Request failed",
                Collections.singletonList(new ErrorDetail("GENERAL_ERROR", ex.getMessage()))
        );
        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Unexpected error",
                Collections.singletonList(new ErrorDetail("SERVER_ERROR", "An unexpected error occurred"))
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}