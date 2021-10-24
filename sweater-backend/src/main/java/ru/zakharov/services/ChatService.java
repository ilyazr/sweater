package ru.zakharov.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zakharov.dto.ChatRoomDTO;
import ru.zakharov.dto.UserDTO;
import ru.zakharov.exceptions.ChatException;
import ru.zakharov.exceptions.UserNotFoundException;
import ru.zakharov.models.User;
import ru.zakharov.models.chat.ChatRoom;
import ru.zakharov.models.chat.ChatRoomWithUnreadMessages;
import ru.zakharov.repos.ChatRoomRepo;
import ru.zakharov.repos.UserRepo;
import ru.zakharov.utils.PrincipalUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatService {

    private ChatRoomRepo chatRoomRepo;
    private UserRepo userRepo;
    private ChatMessageService chatMessageService;
    private UserService userService;

    public ChatRoomDTO mapToDto(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .id(chatRoom.getId())
                .owner(userService.mapToDto(chatRoom.getOwner()))
                .name(chatRoom.getName())
                .capacity(chatRoom.getCapacity())
                .users(userService.mapMultipleUsersToDto(chatRoom.getUsers(), new HashSet<>()))
                .build();
    }

    public List<Integer> getAllIdsOFChatRoomsOfUser(int userId) {
        return chatRoomRepo.getIdsOfAllChatRoomsOfUser(userId);
    }

    public List<ChatRoomWithUnreadMessages> getAllChatRoomsByUserId(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        List<ChatRoomWithUnreadMessages> chatRooms = new ArrayList<>();
        List<ChatRoom> chats = chatRoomRepo.getAllChatRoomsByUserId(userId);
        chats.forEach(chat -> {
            int count = chatMessageService
                    .getCountOfAllSentMessagesByChatRoomIdAndPrincipalId(chat.getId());
            chatRooms.add(new ChatRoomWithUnreadMessages(mapToDto(chat), count));
        });
        return chatRooms;
    }

    public void setOwner(ChatRoom room, User user) {
        if (room.getCapacity() >= 2) {
            room.setOwner(user);
        }
    }

    /**
     *
     * @param ownerId - send friendship request
     * @param friendId - accept friendship request
     * @return new ChatRoom
     */
    public ChatRoomDTO createChatRoom(Integer capacity, Integer ownerId, Integer friendId) {
        ChatRoom chat = null;
        if (capacity == null) {
            chat = createChatRoomWithDefaultCapacity();
        } else if (capacity <= 1 || capacity > 10) {
            throw new ChatException("Capacity should be from 2 to 10");
        } else if (capacity > 2) {
            chat = createChatRoomWithGivenCapacity(capacity);
        } else {
            chat = createChatRoomWithDefaultCapacity();
        }
        User owner = userService.getUserById(ownerId);
        User friend = userService.getUserById(friendId);
        chat.addUserIntoChatRoom(owner);
        chat.addUserIntoChatRoom(friend);
        owner.addChatRoom(chat);
        friend.addChatRoom(chat);
        setOwner(chat, owner);
        chatRoomRepo.save(chat);
        log.info("New chat created: capacity={}, ownerId={}, friendId={}",
                chat.getCapacity(), ownerId, friendId);
        return mapToDto(chat);
    }

    protected ChatRoom createChatRoomWithGivenCapacity(int capacity) {
        ChatRoom chatRoom = new ChatRoom(capacity);
        chatRoom.setOwnerId(PrincipalUtil.getPrincipalId());
        return chatRoom;
    }

    protected ChatRoom createChatRoomWithDefaultCapacity() {
        return new ChatRoom(2);
    }

    public ChatRoomDTO leaveChatRoom(int chatId) {
        // Cases:
        //    1) capacity = 2, one user leaves => delete chat
        //    2) capacity > 2, owner leaves => delete chat
        int principalId = PrincipalUtil.getPrincipalId();
        ChatRoom chatRoom = chatRoomRepo
                .findById(chatId)
                .orElseThrow(() -> new ChatException(String.format(
                        "Chat with id %d doesn't exist", chatId
                )));
        boolean isInChat = chatRoomRepo.isUserInChatRoom(principalId, chatId);
        if (isInChat) {
            boolean condition = (chatRoom.getCapacity() == 2) ||
                    (chatRoom.getOwnerId() != null &&
                            chatRoom.getCapacity() > 2 &&
                            chatRoom.getOwnerId() == principalId);
            if (condition) {
                deleteChat(chatRoom.getId());
            } else {
                chatRoomRepo.leaveChat(principalId, chatId);
            }
        } else {
            throw new ChatException(String.format("User #%d not a member of #%d chat!",
                    principalId, chatRoom.getId()));
        }
        return mapToDto(chatRoom);
    }

    public List<UserDTO> getAllMembersOfChatRoom(int chatRoomId) {
        checkChatRoomExistence(chatRoomId);
        return userRepo.getAllMembersOfChatRoom(chatRoomId).stream()
                .map(userService::mapToDto)
                .collect(Collectors.toList());
    }

    public Integer deleteChatBetweenUsers(int user1, int user2) {
        ChatRoom chatRoom = chatRoomRepo.getChatRoomByFriendId(user1, user2)
                .orElseThrow(() -> new IllegalStateException("chat with that user does not exist"));
        log.info("Chat with id {} has been removed. Users id's: {}, {}",
                chatRoom.getId(), user1, user2);
        return deleteChat(chatRoom.getId());
    }

    public Integer deleteChat(int chatRoomId) {
        int c = chatRoomRepo.deleteChatRoomById(chatRoomId);
        log.info("Affected rows by deletion chatRoom #{}: {}", chatRoomId, c);
        return c;
    }

    private void checkChatRoomExistence(int chatId) {
        if (!chatRoomRepo.existsById(chatId)) {
            throw new ChatException(chatId);
        }
    }

    public ChatRoomDTO addUserIntoChatRoom(int chatId, int userId) {
        ChatRoom chatRoom = chatRoomRepo
                .findById(chatId)
                .orElseThrow(() -> new ChatException(chatId));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (chatRoom.getUsers().contains(user)) {
            throw new ChatException(String.format("User with id %d already in this chat", userId));
        }
        if (chatRoom.getUsers().size() + 1 > chatRoom.getCapacity()) {
            throw new ChatException("Exceeded the maximum number of users in the chat room");
        }
        chatRoom.addUserIntoChatRoom(user);
        user.addChatRoom(chatRoom);
        return mapToDto(chatRoom);
    }

    public ChatRoomDTO getChatRoomByFriendId(int friendId) {
        return mapToDto(chatRoomRepo.getChatRoomByFriendId(PrincipalUtil.getPrincipalId(), friendId)
                .orElseThrow(() -> new IllegalStateException("chat with that user does not exist")));
    }
}
