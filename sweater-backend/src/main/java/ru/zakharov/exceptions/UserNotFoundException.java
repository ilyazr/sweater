package ru.zakharov.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("User with name %s not found!", username));
    }

    public UserNotFoundException(int id) {
        super(String.format("User with id %d not found!", id));
    }
}
