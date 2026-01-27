package ru.skripov.resume_back.security.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skripov.resume_back.security.dto.UserDto;
import ru.skripov.resume_back.security.dto.auth.StateDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginResponseDto;
import ru.skripov.resume_back.security.dto.auth.login.RefreshTokenRequestDto;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.mappers.UserMapper;
import ru.skripov.resume_back.security.services.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и управления сессиями")
public class AuthController {

    @Value("${jwt.cookie-name:jwt}")
    private String cookieName;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @Operation(summary = "Аутентификация пользователя",
            description = "Вход в систему с получением access и refresh токенов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping("/login")
    public ResponseEntity<@NonNull LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequest,
            HttpServletResponse response) {

        log.info("Попытка авторизации для пользователя: {}", loginRequest.getLogin());

        LoginResponseDto loginResponse = authenticationService.login(loginRequest);

        setAuthCookie(response, loginResponse.getAccessToken(), loginResponse.getAccessTokenExpiration());
        setRefreshCookie(response, loginResponse.getRefreshToken(), loginResponse.getRefreshTokenExpiration());

        log.info("Пользователь {} успешно авторизован", loginRequest.getLogin());

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Обновление токена",
            description = "Получение новой пары токенов по refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<@NonNull LoginResponseDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDto refreshRequest,
            HttpServletResponse response) {

        log.info("Запрос на обновление пары токенов");

        LoginResponseDto loginResponse = authenticationService.refreshToken(refreshRequest.getRefreshToken());

        // Обновляем cookies
        setAuthCookie(response, loginResponse.getAccessToken(), loginResponse.getAccessTokenExpiration());
        setRefreshCookie(response, loginResponse.getRefreshToken(), loginResponse.getRefreshTokenExpiration());

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким логином уже существует"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации")
    })
    @PostMapping("/register")
    public ResponseEntity<@NonNull UserDto> register(
            @Valid @RequestBody RegistrationRequestDto registrationRequest) {

        log.info("Попытка регистрации для пользователя: {}", registrationRequest.getEmail());

        UserDto userDto = authenticationService.doRegister(registrationRequest);

        log.info("Пользователь {} успешно зарегистрирован", registrationRequest.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @Operation(summary = "Проверка состояния аутентификации")
    @GetMapping("/state")
    public ResponseEntity<@NonNull StateDto> getAuthenticationState() {
        StateDto state = authenticationService.getAuthenticationState();
        return ResponseEntity.ok(state);
    }

    @Operation(summary = "Выход из системы",
            description = "Очищает аутентификационные cookies")
    @PostMapping("/logout")
    public ResponseEntity<@NonNull Void> logout(HttpServletResponse response) {
        clearCookie(response, cookieName);
        clearCookie(response, cookieName + "_refresh");

        log.info("User logged out");

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получение информации о текущем пользователе")
    @GetMapping("/me")
    public ResponseEntity<@NonNull UserDto> getCurrentUser() {
        try {
            User currentUser = authenticationService.getCurrentUser();
            UserDto userDto = userMapper.toDto(currentUser);

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
        cookie.setSecure(cookieSecure);
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