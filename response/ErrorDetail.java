package com.p2p.transport.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDetail {
    private String code;
    private String message;
}