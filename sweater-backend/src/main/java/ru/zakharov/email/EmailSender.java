package ru.zakharov.email;

public interface EmailSender {
    void send(String to, String text);
}
