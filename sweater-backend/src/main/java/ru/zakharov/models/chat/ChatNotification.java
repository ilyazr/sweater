package ru.zakharov.models.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatNotification {
    private int id;
    private int senderId;
    private int recipientId;

    public ChatNotification(int senderId, int recipientId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
    }

}
