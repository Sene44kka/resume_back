package ru.skripov.resume_back.security.dto.auth.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class RegistrationRequestDto implements Serializable {
    @Schema(name = "firstName", description = "Имя", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(name = "lastName", description = "Фамилия", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(name = "login", description = "Логин", requiredMode = Schema.RequiredMode.REQUIRED)
    private String login;

    @Schema(name = "password", description = "Пароль", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
