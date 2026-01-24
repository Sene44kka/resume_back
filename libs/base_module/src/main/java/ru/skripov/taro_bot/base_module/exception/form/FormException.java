package ru.skripov.resume_back.base_module.exception.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FormException extends RuntimeException {
    private final String code;
    private final String field;
    private final String message;

    public FormException(String code, String field, String message) {
        super(message);

        this.code = code;
        this.field = field;
        this.message = message;
    }
}

