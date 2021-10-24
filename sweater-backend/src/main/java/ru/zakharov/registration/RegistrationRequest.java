package ru.zakharov.registration;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String username;
    private final String password;
    private final String email;
    private final String dateOfBirth;

    public Timestamp getDateOfBirth() {
        return Timestamp.valueOf(String.format("%s 00:00:00", this.dateOfBirth));
    }
}
