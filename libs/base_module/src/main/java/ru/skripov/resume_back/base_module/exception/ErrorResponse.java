package ru.skripov.resume_back.base_module.exception;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private int status;
    @Builder.Default
    private String timestamp = Instant.now().toString();
}
