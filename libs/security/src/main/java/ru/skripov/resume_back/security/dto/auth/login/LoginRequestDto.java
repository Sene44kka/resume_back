package ru.skripov.resume_back.security.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Schema(description = "Запрос на аутентификацию")
public class LoginRequestDto implements Serializable {
    @NotBlank(message = "Логин не может быть пустым")
    @Schema(name = "login", description = "Логин", requiredMode = Schema.RequiredMode.REQUIRED)
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(name = "password", description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
