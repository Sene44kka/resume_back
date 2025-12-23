package ru.skripov.resume_back.security.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skripov.resume_back.security.dto.*;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginResponseDto;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.mappers.UserMapper;

@Service
@Slf4j
public class AuthenticationService {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Autowired
    private AuthenticationService(UserService userService, TokenService tokenService,
                                  AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        try {
            // Аутентификация через Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Получаем пользователя
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByLogin(userDetails.getUsername());

            // Генерируем токены
            TokenService.TokenPair tokenPair = tokenService.generateTokenPair(user);

            log.info("User {} successfully logged in", user.getLogin());

            return LoginResponseDto.builder()
                    .accessToken(tokenPair.accessToken())
                    .refreshToken(tokenPair.refreshToken())
                    .expiresAt(tokenPair.expiresAt())
                    .user(userMapper.toDto(user))
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for user: {}", request.getLogin());
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        if (!tokenService.isValidToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Извлекаем пользователя из refresh token
        String username = tokenService.getTokenInfo(refreshToken).get("username").toString();
        User user = userService.findByLogin(username);

        // Генерируем новую пару токенов
        TokenService.TokenPair tokenPair = tokenService.generateTokenPair(user);

        return LoginResponseDto.builder()
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .expiresAt(tokenPair.expiresAt())
                .user(userMapper.toDto(user))
                .build();
    }

    // ... остальные методы остаются без изменений
}