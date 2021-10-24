package ru.zakharov.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UsernameAndPasswordRequest {
    private String username;
    private String password;
}
