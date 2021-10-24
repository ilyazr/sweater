package ru.zakharov.exceptions;

public class ChatException extends RuntimeException {
    public ChatException(String message) {
        super(message);
    }

    public ChatException(int chatId) {
        this(String.format("ChatRoom with id %d doesn't exist", chatId));
    }
}
