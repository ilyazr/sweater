package ru.zakharov.models.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

@Entity
@Table(name = "chat_message")
@Getter
@Setter
@ToString
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sender_id")
    @NotNull(message = "sender id can't be null")
    private Integer senderId;

    @Column(name = "sender_username")
    private String senderUsername;

    @Column(name = "sender_fullname")
    private String senderFullName;

    @Column(name = "recipient_id")
    @NotNull(message = "recipient id can't be null")
    private Integer recipientId;

    @Column(name = "chat_room_id")
    private Integer chatRoomId;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Calendar createdAt;

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    public ChatMessage() {
        setTimestampToNow();
    }

    public ChatMessage(Integer senderId,
                       String senderUsername,
                       String senderFullName,
                       Integer recipientId,
                       Integer chatRoomId,
                       String text) {
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.senderFullName = senderFullName;
        this.recipientId = recipientId;
        this.chatRoomId = chatRoomId;
        this.text = text;
        createdAt = Calendar.getInstance();
    }

    public ChatMessage(Integer senderId,
                       Integer recipientId,
                       Integer chatRoomId,
                       String text,
                       Calendar createdAt) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.chatRoomId = chatRoomId;
        this.text = text;
        this.createdAt = createdAt;
    }

    private void setTimestampToNow() {
        setCreatedAt(Calendar.getInstance());
    }
}
