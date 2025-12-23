package ru.skripov.resume_back.security.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginRequestDto implements Serializable {
    @Schema(name = "login", description = "Логин", requiredMode = Schema.RequiredMode.REQUIRED)
    private String login;

    @Schema(name = "password", description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
