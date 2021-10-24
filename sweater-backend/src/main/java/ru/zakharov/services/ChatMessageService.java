package ru.zakharov.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.zakharov.exceptions.ChatException;
import ru.zakharov.models.chat.ChatMessage;
import ru.zakharov.models.chat.MessageStatus;
import ru.zakharov.repos.ChatMessageRepo;
import ru.zakharov.repos.ChatRoomRepo;
import ru.zakharov.repos.OnlineUserRepo;
import ru.zakharov.utils.PrincipalUtil;
import ru.zakharov.utils.TransactionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepo chatMessageRepo;
    private final ChatRoomRepo chatRoomRepo;
    private final OnlineUserRepo onlineUserRepo;
    private final TransactionHandler transactionHandler;

    public void processMessage(ChatMessage message) {
        processPrivateMessage(message);
    }

    public void processPrivateMessage(ChatMessage message) {
        Integer recipientId = message.getRecipientId();
        boolean isOnline = onlineUserRepo.existsByUserId(recipientId);
        message.setStatus(MessageStatus.SENT);
        ChatMessage saved = chatMessageRepo.save(message);
        sendPrivateMessage(saved);
    }

    public void sendPrivateMessage(ChatMessage message) {
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId().toString(),
                "/queue/pm",
                message
        );
    }

    public void sendMessageIntoGroupChat(ChatMessage message) {
        //TODO: Implement logic when you will create group chat mechanics
        throw new RuntimeException("This method hasn't been realized yet");
    }

    public void sendNotification() {
        throw new RuntimeException("This method hasn't been realized yet");
    }

    public List<ChatMessage> getAllMessages() {
        return chatMessageRepo.findAll();
    }

    public int getCountOfUnreadMessages(int senderId, int recipientId) {
        return chatMessageRepo.getCountOfUnreadMessages(senderId, recipientId);
    }

    public List<ChatMessage> getAllSentMessages(int senderId, int recipientId) {
        return chatMessageRepo
                .getSentChatMessageSortedByCreatedAt(senderId, recipientId);
    }

    public List<ChatMessage> getAllDeliveredMessages(int senderId, int recipientId) {
        return chatMessageRepo.getDeliveredChatMessageSortedByCreatedAt(senderId, recipientId);
    }

    public List<ChatMessage> getAllDeliveredMessagesByChatRoomId(int chatId) {
        if (!chatRoomRepo.existsById(chatId)) {
            throw new ChatException(chatId);
        }
        return chatMessageRepo.getAllDeliveredMessagesByChatRoomId(chatId);
    }

    public List<ChatMessage> getAllSentMessagesByChatRoomId(int chatId) {
        int principalId = PrincipalUtil.getPrincipalId();
        if (!chatRoomRepo.existsById(chatId)) {
            throw new ChatException(chatId);
        }
        return chatMessageRepo.getAllSentMessagesByChatRoomId(chatId)
                .stream()
                .map(msg -> {
                    if (msg.getSenderId() != principalId) {
                        msg.setStatus(MessageStatus.DELIVERED);
                    }
                    return chatMessageRepo.save(msg);
                })
                .collect(Collectors.toList());
    }

    public int getCountOfAllSentMessagesByChatRoomIdAndPrincipalId(int chatRoomId) {
        if (!chatRoomRepo.existsById(chatRoomId)) {
            throw new ChatException(chatRoomId);
        }
        return chatMessageRepo.getCountOfUnreadMessagesOfPrincipal(chatRoomId, PrincipalUtil.getPrincipalId());
    }

    public int readMessageInChatRoom(int msgId) {
        return transactionHandler.runInTransaction(() -> {
            return chatMessageRepo.readMessageInChatRoom(msgId);
        });
    }
}
