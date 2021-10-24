package ru.zakharov.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.zakharov.models.chat.ChatMessage;
import ru.zakharov.services.ChatMessageService;
import ru.zakharov.utils.ControllersUtil;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/pm")
    public void processPrivateMessages(@Valid @Payload ChatMessage message) {
        log.info("Income chat message: {}", message);
        chatMessageService.processMessage(message);
    }

    @GetMapping("/msg/chat/{chatId}/sent/count")
    public ResponseEntity<?> getCountOfAllSentMessagesByChatRoomId(@PathVariable(name = "chatId") int chatId) {
        int count = chatMessageService.getCountOfAllSentMessagesByChatRoomIdAndPrincipalId(chatId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/msg/chat/{chatId}/sent")
    public ResponseEntity<?> getAllSentMessagesByChatRoomId(@PathVariable(name = "chatId") int chatId) {
        List<ChatMessage> sent = chatMessageService.getAllSentMessagesByChatRoomId(chatId);
        return ResponseEntity.ok(sent);
    }

    @GetMapping("/msg/chat/{chatId}/delivered")
    public ResponseEntity<?> getAllDeliveredMessagesByChatRoomId(@PathVariable(name = "chatId") int chatId) {
        List<ChatMessage> delivered = chatMessageService.getAllDeliveredMessagesByChatRoomId(chatId);
        return ResponseEntity.ok(delivered);
    }

    @GetMapping("/msg/all")
    public ResponseEntity<?> getAllMessages() {
        return ResponseEntity.ok(chatMessageService.getAllMessages());
    }

    @GetMapping("/msg/sent/{senderId}/{recipientId}/count")
    public ResponseEntity<?> getCountOfUnreadMessages(@PathVariable(name = "senderId") int senderId,
                                                      @PathVariable(name = "recipientId") int recipientId) {
        int count = chatMessageService.getCountOfUnreadMessages(senderId, recipientId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("count", count);
        body.put("senderId", senderId);
        body.put("recipientId", recipientId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/msg/sent/{senderId}/{recipientId}")
    public ResponseEntity<?> getAllSentMessagesBySenderAndRecipient(@PathVariable(name = "senderId") int senderId,
                                                @PathVariable(name = "recipientId") int recipientId) {
        List<ChatMessage> sentMessages = chatMessageService.getAllSentMessages(senderId, recipientId);
        return ResponseEntity.ok(sentMessages);
    }

    @GetMapping("/msg/delivered/{senderId}/{recipientId}")
    public ResponseEntity<?> getAllDeliveredMessages(@PathVariable(name = "senderId") int senderId,
                                                @PathVariable(name = "recipientId") int recipientId) {
        List<ChatMessage> sentMessages = chatMessageService.getAllDeliveredMessages(senderId, recipientId);
        return ResponseEntity.ok(sentMessages);
    }

    @GetMapping("/msg/read/{msgId}")
    public ResponseEntity<?> readMessageInChatRoom(@PathVariable(name = "msgId") int msgId) {
        log.debug("/msg/read/{}", msgId);
        int rowsAffected = chatMessageService.readMessageInChatRoom(msgId);
        log.debug("rowsAffected = {}", rowsAffected);
        return ResponseEntity.ok(ControllersUtil.composeResponse(
                "rowsAffected", rowsAffected
        ));
    }

}
