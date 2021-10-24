package ru.zakharov.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.zakharov.dto.ChatRoomDTO;
import ru.zakharov.dto.UserDTO;
import ru.zakharov.models.chat.ChatRoomWithUnreadMessages;
import ru.zakharov.services.ChatService;
import ru.zakharov.utils.ControllersUtil;
import ru.zakharov.utils.PrincipalUtil;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@AllArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/members/{chatRoomId}")
    public ResponseEntity<?> getAllMembersOfChatRoom(@PathVariable(name = "chatRoomId") int chatRoomId) {
        List<UserDTO> members = chatService.getAllMembersOfChatRoom(chatRoomId);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                    "chatId", chatRoomId,
                    "numberOfUsers", members.size(),
                    "users", members
                ));
    }

    @GetMapping("/getAll/ids")
    public ResponseEntity<?> getAllIdsOChatRoomsOfPrincipal() {
        int principalId = PrincipalUtil.getPrincipalId();
        Integer[] ids = chatService.getAllIdsOFChatRoomsOfUser(principalId).toArray(Integer[]::new);
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/getAll/ids/{userId}")
    public ResponseEntity<?> getAllIdsOfChatRoomsOfUser(@PathVariable(name = "userId") int userId) {
        Integer[] ids = chatService.getAllIdsOFChatRoomsOfUser(userId).toArray(Integer[]::new);
        return ResponseEntity.ok(ids);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllChatRoomsByUserId(@RequestParam(name = "userId") int userId) {
        List<ChatRoomWithUnreadMessages> allChatRoomsByUserIdWithCountOfUnreadMsgs =
                chatService.getAllChatRoomsByUserId(userId);
        return ResponseEntity.ok(allChatRoomsByUserIdWithCountOfUnreadMsgs);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(
            @RequestParam(name = "cap", required = false) Integer cap,
            @RequestParam(name = "ownerId") Integer ownerId,
            @RequestParam(name = "friendId") Integer friendId) {
        ChatRoomDTO chatRoom = chatService.createChatRoom(cap, ownerId, friendId);
        return ResponseEntity.ok(chatRoom);
    }

    @DeleteMapping("/leave/{chatId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable(name = "chatId") int chatId) {
        ChatRoomDTO chatRoom = chatService.leaveChatRoom(chatId);
        return ResponseEntity.ok(Map.of(
                "chatId", chatRoom,
                "action", "leave"
        ));
    }

    @PostMapping("/{chatId}/addUser")
    public ResponseEntity<?> addUserIntoChat(@PathVariable(name = "chatId") int chatId) {
        ChatRoomDTO chatRoom = chatService.addUserIntoChatRoom(chatId, PrincipalUtil.getPrincipalId());
        return ResponseEntity.ok(chatRoom);
    }

    @PostMapping("/{chatId}/addUser/{userId}")
    public ResponseEntity<?> addUserIntoChat(@PathVariable(name = "chatId") int chatId,
                                           @PathVariable(name = "userId") int userId) {
        ChatRoomDTO chatRoom = chatService.addUserIntoChatRoom(chatId, userId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/get")
    public ResponseEntity<?> getChatRoomByFriendId(@RequestParam(name = "friendId") int friendId) {
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "chatRoom", chatService.getChatRoomByFriendId(friendId)
        ));
    }

}
