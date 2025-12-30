package ru.skripov.resume_back.security.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.skripov.resume_back.security.dto.UserDto;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на успешную аутентификацию")
public class LoginResponseDto implements Serializable {

    @Schema(description = "Access token для авторизации", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Refresh token для обновления access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Дата истечения действия access token", example = "2025-12-23T12:00:00.000Z")
    private Date expiresAt;

    @Schema(description = "Информация о пользователе")
    private UserDto user;
}