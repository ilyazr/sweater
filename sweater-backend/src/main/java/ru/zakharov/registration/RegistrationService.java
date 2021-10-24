package ru.zakharov.registration;

import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import ru.zakharov.email.EmailSender;
import ru.zakharov.models.User;
import ru.zakharov.registration.token.ConfirmationToken;
import ru.zakharov.registration.token.ConfirmationTokenService;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.services.UserService;
import ru.zakharov.utils.TransactionHandler;

import java.net.InetAddress;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final UserRepo userRepo;
    private final EmailSender emailSender;
    private final Environment environment;
    private final EmailValidator emailValidator;
    private final TransactionHandler transactionHandler;
    private final ConfirmationTokenService confirmationTokenService;

    public void sendEmailForConfirmation(RegistrationRequest req, String token) {
        String link = buildLink(token);
        String emailText = buildEmail(req.getUsername(), link);
        emailSender.send(req.getEmail(), emailText);
    }

    public String register(RegistrationRequest req) {
        boolean isEmailValid = emailValidator.test(req.getEmail());
        if (!isEmailValid) throw new IllegalStateException("Email not valid");

        String token = userService.signUpUser(new User(
                req.getUsername(),
                req.getPassword(),
                req.getEmail(),
                req.getDateOfBirth(),
                false)
        );

        sendEmailForConfirmation(req, token);

        return token;
    }

    public String recreateConfirmationToken(RegistrationRequest req) {
        User user = userRepo.findUserByEmail(req.getEmail())
                .orElseThrow(() ->
                        new IllegalStateException(String.format("user with email %s wasn't found!",
                                req.getEmail())));
        if (user.getEnabled()) throw new IllegalStateException("account already confirmed");
        int affectedRows =
                confirmationTokenService.deleteConfirmationTokensForUser(user.getId());
        ConfirmationToken confirmationToken =
                confirmationTokenService.createConfirmationTokenForUserAndSave(user);

        String token = confirmationToken.getToken();

        sendEmailForConfirmation(req, token);
        return token;
    }

    public String confirmToken(String token) {
        return transactionHandler.runInTransaction(() -> {
            ConfirmationToken confirmationToken = confirmationTokenService
                    .getToken(token)
                    .orElseThrow(() -> new IllegalStateException("token not found"));

            if (confirmationToken.getConfirmedAt() != null) {
                throw new IllegalStateException("email already confirmed");
            }

            LocalDateTime expiresAt = confirmationToken.getExpiresAt();
            if (expiresAt.isBefore(LocalDateTime.now())) {
                throw new IllegalStateException("confirmation token expired");
            }

            confirmationTokenService.setConfirmedAt(token);
            userService.enableUser(confirmationToken.getUser().getEmail());
            return "confirmed";
        });
    }

    private String buildLink(String token) {
        final String schema = "http";
        final String hostAddress = InetAddress.getLoopbackAddress().getHostAddress();
        final String serverPort = environment.getProperty("server.port");
        return String.format("%s://%s:%s/api/registration/confirm?token=%s",
                schema, hostAddress, serverPort, token);
    }

    public String buildEmail(String username, String link) {
        return """
               <h1>Hello %s!</h1>
               <p>Thank you for registering. Click <a href="%s">here</a> to activate you account</p>
                """.formatted(username, link);
    }
}
