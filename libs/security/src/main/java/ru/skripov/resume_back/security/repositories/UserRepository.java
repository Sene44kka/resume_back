package ru.skripov.resume_back.security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.resume_back.stereotype.Repository;
import ru.skripov.resume_back.security.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    User findUserByLoginAndPassword(String login, String password);
}
