package ru.zakharov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FriendshipDTO {

    private List<UserDTO> accepted;
    private List<UserDTO> requested;
    private List<UserDTO> outgoingRequests;

}
