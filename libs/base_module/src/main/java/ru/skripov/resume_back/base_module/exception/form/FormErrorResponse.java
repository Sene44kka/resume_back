package ru.skripov.resume_back.base_module.exception.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormErrorResponse {
    private int status;
    private String code;
    private String field;
    private String message;
}
