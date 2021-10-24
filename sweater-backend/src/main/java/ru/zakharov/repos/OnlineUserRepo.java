package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakharov.models.OnlineUser;

import java.util.Optional;

public interface OnlineUserRepo extends JpaRepository<OnlineUser, Integer> {
    void removeBySessionId(String sessionId);
    Optional<OnlineUser> findBySessionId(String sessionId);
    boolean existsByUserId(Integer userId);
}
