package ru.skripov.resume_back.security.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.skripov.resume_back.base_module.exception.ErrorResponse;
import ru.skripov.resume_back.security.dto.UserDto;
import ru.skripov.resume_back.security.dto.auth.StateDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginResponseDto;
import ru.skripov.resume_back.security.dto.auth.login.RefreshTokenRequestDto;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.services.AuthenticationService;

import javax.security.sasl.AuthenticationException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и управления сессиями")
public class AuthController {

    @Value("${jwt.access-token-expiration:900}")  // 15 минут по умолчанию
    private Integer accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800}")  // 7 дней по умолчанию
    private Integer refreshTokenExpiration;

    @Value("${jwt.cookie-name:jwt}")
    private String cookieName;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private final AuthenticationService authenticationService;

    @Operation(summary = "Аутентификация пользователя",
            description = "Вход в систему с получением access и refresh токенов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {

        log.info("Login attempt for user: {}", loginRequest.getLogin());

        LoginResponseDto loginResponse = authenticationService.login(loginRequest);

        // Устанавливаем access token в cookie (опционально)
        setAuthCookie(response, loginResponse.getAccessToken(), accessTokenExpiration);

        // Можно также установить refresh token в отдельную cookie
        setRefreshCookie(response, loginResponse.getRefreshToken(), refreshTokenExpiration);

        log.info("User {} successfully authenticated", loginRequest.getLogin());

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Обновление токена",
            description = "Получение новой пары токенов по refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto refreshRequest,
            HttpServletResponse response) {

        log.info("Token refresh request");

        LoginResponseDto loginResponse = authenticationService.refreshToken(refreshRequest.getRefreshToken());

        // Обновляем cookies
        setAuthCookie(response, loginResponse.getAccessToken(), accessTokenExpiration);
        setRefreshCookie(response, loginResponse.getRefreshToken(), refreshTokenExpiration);

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким логином уже существует"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(
            @Valid @RequestBody RegistrationRequestDto registrationRequest) {

        log.info("Registration attempt for user: {}", registrationRequest.getLogin());

        UserDto userDto = authenticationService.doRegister(registrationRequest);

        log.info("User {} successfully registered", registrationRequest.getLogin());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Operation(summary = "Проверка состояния аутентификации")
    @GetMapping("/state")
    public ResponseEntity<StateDto> getAuthenticationState() {
        StateDto state = authenticationService.getAuthenticationState();
        return ResponseEntity.ok(state);
    }

    @Operation(summary = "Выход из системы",
            description = "Очищает аутентификационные cookies")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Очищаем access token cookie
        clearCookie(response, cookieName);

        // Очищаем refresh token cookie
        clearCookie(response, cookieName + "_refresh");

        log.info("User logged out");

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение информации о текущем пользователе")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        try {
            UserDto userDto = authenticationService.getCurrentUserDto();
            return ResponseEntity.ok(userDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private void setAuthCookie(HttpServletResponse response, String token, int maxAge) {
        Cookie cookie = createCookie(cookieName, token, maxAge);
        response.addCookie(cookie);
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken, int maxAge) {
        Cookie cookie = createCookie(cookieName + "_refresh", refreshToken, maxAge);
        response.addCookie(cookie);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);  // true для production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        cookie.setAttribute("SameSite", "Strict");

        return cookie;
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}