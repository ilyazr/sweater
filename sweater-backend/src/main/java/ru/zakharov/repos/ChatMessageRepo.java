package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.zakharov.models.chat.ChatMessage;

import javax.transaction.Transactional;
import java.util.List;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, Integer> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update chat_message set status = 'DELIVERED' where id=:msgId",
            nativeQuery = true)
    int readMessageInChatRoom(int msgId);

    @Query(value = "select exists(select 1 from chat_room where id=:chatId)",
            nativeQuery = true)
    boolean checkIfChatRoomExistsById(int chatId);

    @Query(value = "select count(*) from chat_message " +
            "where chat_room_id=:chatRoomId and recipient_id=:principalId and status = 'SENT'",
            nativeQuery = true)
    Integer getCountOfUnreadMessagesOfPrincipal(int chatRoomId, int principalId);

    @Query(value = "select count(*) from chat_message " +
            "where chat_room_id=:chatRoomId and status = 'SENT'",
            nativeQuery = true)
    Integer getCountOfUnreadMessages(int chatRoomId);

    @Query(value = "select * from chat_message " +
            "where chat_room_id=:chatRoomId and status = 'SENT' " +
            "order by created_at",
            nativeQuery = true)
    List<ChatMessage> getAllSentMessagesByChatRoomId(int chatRoomId);

    @Query(value = "select * from chat_message " +
            "where chat_room_id=:chatRoomId and status = 'DELIVERED' " +
            "order by created_at",
            nativeQuery = true)
    List<ChatMessage> getAllDeliveredMessagesByChatRoomId(int chatRoomId);

    @Query(value = "select * from chat_message " +
            "where status = 'SENT' " +
            "and sender_id=:senderId " +
            "and recipient_id=:recipientId " +
            "order by created_at",
            nativeQuery = true)
    List<ChatMessage> getSentChatMessageSortedByCreatedAt(int senderId, int recipientId);

    @Query(value = "select * from chat_message " +
            "where status = 'DELIVERED' " +
            "and sender_id=:senderId " +
            "and recipient_id=:recipientId " +
            "order by created_at",
            nativeQuery = true)
    List<ChatMessage> getDeliveredChatMessageSortedByCreatedAt(int senderId, int recipientId);

    @Query(value = "select count(*) from chat_message " +
            "where status = 'SENT' " +
            "and sender_id=:senderId " +
            "and recipient_id=:recipientId",
            nativeQuery = true)
    Integer getCountOfUnreadMessages(int senderId, int recipientId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update chat_message set status=:status " +
            "where id=:msgId",
            nativeQuery = true)
    void updateStatus(int msgId, String status);

}
