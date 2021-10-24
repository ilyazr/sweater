package ru.zakharov.models.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.zakharov.models.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @Column(name = "owner_id", insertable = false, updatable = false)
    private Integer ownerId;

    @Column(name = "name")
    private String name;

    @Column(name = "capacity")
    private int capacity;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "chat_room_users",
            joinColumns = {@JoinColumn(name = "chat_room_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users = new HashSet<>();

    public ChatRoom(String name) {
        this.name = name;
    }

    public ChatRoom(Integer capacity) {
        this.capacity = capacity;
    }

    public ChatRoom(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public void addUserIntoChatRoom(User user) {
        getUsers().add(user);
    }

}
