package ru.zakharov.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update confirmation_tokens " +
            "set confirmed_at=:confirmedAt " +
            "where token=:token",
            nativeQuery = true)
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from confirmation_tokens where user_id=:userId",
            nativeQuery = true)
    int deleteTokensForUser(int userId);
}
