package ru.skripov.resume_back.security.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoginResponseDto implements Serializable {
    @Schema(name = "token", description = "JWT токен")
    private String token;
}
