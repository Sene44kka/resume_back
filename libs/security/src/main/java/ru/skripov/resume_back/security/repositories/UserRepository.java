package ru.skripov.resume_back.security.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skripov.resume_back.security.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserByEmailAndPassword(String login, String password);
}
