package ru.zakharov.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WallMessageDTO {

    private int id;
    private Timestamp createdAt;
    private String imgURI;
    private String text;
    private Timestamp updatedAt;
    private int authorId;
    private Boolean isLiked;
    private String authorFirstName;
    private String authorLastName;
    private String authorUsername;
    private int amountOfLikes;


}
