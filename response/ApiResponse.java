package com.p2p.transport.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;
    private List<ErrorDetail> errors;

    // Constructor for successful responses
    public ApiResponse(int status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Constructor for error responses
    public ApiResponse(int status, String message, T data, List<ErrorDetail> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }
}