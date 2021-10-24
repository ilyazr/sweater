package ru.zakharov.registration.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.zakharov.models.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepo confirmationTokenRepo;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepo.save(token);
    }

    public ConfirmationToken createTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        return new ConfirmationToken(
                token,
                now,
                now.plusMinutes(15),
                user
        );
    }

    public int deleteConfirmationTokensForUser(int userId) {
        return confirmationTokenRepo.deleteTokensForUser(userId);
    }

    public ConfirmationToken createConfirmationTokenForUserAndSave(User user) {
        ConfirmationToken confirmationToken = createTokenForUser(user);
        saveConfirmationToken(confirmationToken);
        return confirmationToken;
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepo.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepo.updateConfirmedAt(token, LocalDateTime.now());
    }

}
