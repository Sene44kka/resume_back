package ru.skripov.resume_back.security.services;

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
import ru.skripov.resume_back.security.dto.auth.StateDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginResponseDto;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.mappers.UserMapper;

import java.util.Optional;

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

        String username = tokenService.getTokenInfo(refreshToken).get("username").toString();
        User user = userService.findByLogin(username);

        TokenService.TokenPair tokenPair = tokenService.generateTokenPair(user);

        return LoginResponseDto.builder()
                .accessToken(tokenPair.accessToken())
                .refreshToken(tokenPair.refreshToken())
                .expiresAt(tokenPair.expiresAt())
                .user(userMapper.toDto(user))
                .build();
    }

    public UserDto doRegister(RegistrationRequestDto registrationRequestDto) {
        try {
            User user = userService.registerUser(registrationRequestDto);

            return Optional.ofNullable(user)
                    .map(userMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("Registration Error: Try Later"));

        } catch (Exception e) {
            throw new RuntimeException("Registration Error: " + e.getMessage());
        }
    }

    public StateDto getAuthenticationState() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String
                && "anonymousUser".equals(authentication.getPrincipal()));

        return new StateDto(isAuthenticated);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return (User) principal;
            }
        }
        throw new RuntimeException("Пользователь не авторизован");
    }

    public UserDto getCurrentUserDto() {
        User currentUser = getCurrentUser();

        return userMapper.toDto(currentUser);
    }
}