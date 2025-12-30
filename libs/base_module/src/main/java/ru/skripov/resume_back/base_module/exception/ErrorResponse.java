package ru.skripov.resume_back.base_module.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private long timestamp;
}
