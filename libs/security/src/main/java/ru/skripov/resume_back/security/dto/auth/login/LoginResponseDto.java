package ru.skripov.resume_back.security.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import ru.skripov.resume_back.security.dto.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
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

    @Schema(description = "Время жизни access token в секундах", example = "900")
    private int accessTokenExpiration;

    @Schema(description = "Время жизни refresh token в секундах", example = "604800")
    private int refreshTokenExpiration;

    @Schema(description = "Дата истечения действия access token", example = "2025-12-23T12:00:00.000Z")
    private Date accessTokenExpiresAt;

    @Schema(description = "Дата истечения действия refresh token", example = "2025-12-23T12:00:00.000Z")
    private Date refreshTokenExpiresAt;

    @Schema(description = "Информация о пользователе")
    private UserDto user;
}