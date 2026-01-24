package ru.skripov.resume_back.security.services;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skripov.resume_back.security.entities.User;
import ru.skripov.resume_back.security.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}