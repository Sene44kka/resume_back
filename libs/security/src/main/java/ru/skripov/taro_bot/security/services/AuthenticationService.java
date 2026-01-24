package ru.skripov.resume_back.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.skripov.resume_back.security.dto.UserDto;
import ru.skripov.resume_back.security.dto.auth.StateDto;
import ru.skripov.resume_back.security.dto.auth.login.LoginRequestDto;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.mappers.UserMapper;
import ru.skripov.resume_back.security.utils.JwtTokenUtil;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationService(UserService userService, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager, UserMapper userMapper) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public String doLogin(LoginRequestDto authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getLogin(),
                            authRequest.getPassword()
                    )
            );

            final UserDetails userDetails = userService
                    .loadUserByUsername(authRequest.getLogin());

            return jwtTokenUtil.generateToken(userDetails);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("User with login or password not found");
        }
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
}