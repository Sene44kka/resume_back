package ru.skripov.resume_back.security.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skripov.resume_back.security.dto.auth.StateDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginResponseDto;
import ru.skripov.resume_back.security.services.AuthenticationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {
    @Value("${jwt.expiration}")
    private Integer jwtExpiration;

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorization success",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authorization error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDto authRequest, HttpServletResponse response) {
        try {
            final String token = authenticationService.doLogin(authRequest);

            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
//            cookie.setSecure(true); // Только для HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(jwtExpiration); // 7 дней
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginResponseDto(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authorization error: " + e.getMessage());
        }
    }

//    @Operation(summary = "Регистрация пользователя")
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequestDto registrationRequestDto) {
//        try {
//            UserDto userDto = authenticationService.doRegister(registrationRequestDto);
//
//            return ResponseEntity.status(HttpStatus.OK).body(userDto);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//                    .body(e.getMessage());
//        }
//    }

    @Operation(summary = "Проверка активности сессии")
    @GetMapping("/state")
    public ResponseEntity<?> getAuthenticationState() {
        try {
            StateDto stateDto = authenticationService.getAuthenticationState();

            return ResponseEntity.status(HttpStatus.OK).body(stateDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "Выход")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }
}