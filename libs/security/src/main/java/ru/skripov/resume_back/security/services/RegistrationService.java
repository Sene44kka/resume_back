package ru.skripov.resume_back.security.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skripov.resume_back.base_module.exception.common.CommonException;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.repositories.UserRepository;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegistrationRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElse(null);

        if (user != null) {
            throw new CommonException("REGISTRATION_FAILED_USER_EXIST", "Пользователь уже существует", HttpStatus.BAD_REQUEST);
        }

        User newUser = new User();
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setMiddleName(dto.getMiddleName());
        newUser.setDateOfBirth(dto.getDateOfBirth());

        return userRepository.save(newUser);
    }
}
