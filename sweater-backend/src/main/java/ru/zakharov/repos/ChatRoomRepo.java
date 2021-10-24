package ru.zakharov.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.zakharov.models.chat.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, Integer> {

    @Query(value = "select chat_room_id from chat_room_users where user_id=:userId",
            nativeQuery = true)
    List<Integer> getIdsOfAllChatRoomsOfUser(int userId);

    @Query(value = "select exists(select 1 from chat_room_users " +
            "where chat_room_id=:cId and user_id=:uId)",
            nativeQuery = true)
    boolean isUserInChatRoom(@Param("uId") int userId, @Param("cId") int chatId);

    @Modifying
    @Query(value = "delete from chat_room_users where chat_room_id=:chatId and user_id=:userId",
            nativeQuery = true)
    void leaveChat(int userId, int chatId);

    @Query(value = "select * from chat_room cr " +
            "join chat_room_users cru " +
            "on cr.id = cru.chat_room_id where user_id=:userId",
            nativeQuery = true)
    List<ChatRoom> getAllChatRoomsByUserId(int userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "delete from chat_room where id=:chatRoomId",
            nativeQuery = true)
    Integer deleteChatRoomById(int chatRoomId);

    @Query(value = "select cr.* from chat_room cr " +
            "    inner join chat_room_users cru on cr.id = cru.chat_room_id " +
            "where (cr.owner_id = :principalId and capacity = 2 and cru.user_id = :friendId) " +
            "or (cr.owner_id = :friendId and capacity = 2 and cru.user_id = :principalId)",
            nativeQuery = true)
    Optional<ChatRoom> getChatRoomByFriendId(int principalId, int friendId);
}
