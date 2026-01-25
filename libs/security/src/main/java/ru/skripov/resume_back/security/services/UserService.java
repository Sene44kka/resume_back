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

    public User findByEmail(String login) {
        return userRepository.findByEmail(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findUserByEmailAndPassword(email, password)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}