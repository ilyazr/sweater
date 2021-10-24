package ru.zakharov.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "online_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// online handled by websocket connection
public class OnlineUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username")
    private String username;

    @Column(name = "session_id")
    // websocket session id
    private String sessionId;

    public OnlineUser(Integer userId, String username, String sessionId) {
        this.userId = userId;
        this.username = username;
        this.sessionId = sessionId;
    }
}
