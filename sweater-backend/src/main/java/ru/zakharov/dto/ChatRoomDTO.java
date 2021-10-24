package ru.zakharov.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    private int id;
    private UserDTO owner;
    private String name;
    private int capacity;
    private Set<UserDTO> users;

}
