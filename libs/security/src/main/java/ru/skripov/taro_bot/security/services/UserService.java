package ru.skripov.resume_back.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skripov.resume_back.security.dto.auth.registration.RegistrationRequestDto;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByLoginAndPassword(String login, String password) {
        User user = userRepository.findUserByLoginAndPassword(login, password);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User registerUser(RegistrationRequestDto dto) {
        User user = userRepository.findByLogin(dto.getLogin())
                .orElse(null);

        if (user != null) {
            throw new RuntimeException("User already exists");
        }

        User newUser = new User();
        newUser.setLogin(dto.getLogin());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());

        return userRepository.save(newUser);
    }
}