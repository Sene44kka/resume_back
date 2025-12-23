package ru.skripov.resume_back.base_module.dto;

public class CustomExceptionResponseDto {
    String code;
    String field;
    String message;

    public CustomExceptionResponseDto(String code, String field, String message) {
        this.code = code;
        this.field = field;
        this.message = message;

    }
}
