package ru.zakharov.models.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.zakharov.dto.ChatRoomDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomWithUnreadMessages {
    private ChatRoomDTO chatRoom;
    private int countOfUnreadMessages;
}
