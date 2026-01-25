package ru.skripov.resume_back.base_module.exception.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class CommonException extends RuntimeException {
    private final String code;
    private final String message;
    private final HttpStatus status;

    public CommonException(String code, String message, HttpStatus status) {
        super(message);

        this.code = code;
        this.message = message;
        this.status = status;
    }
}

