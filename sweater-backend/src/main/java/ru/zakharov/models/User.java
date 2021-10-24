package ru.zakharov.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.zakharov.models.chat.ChatRoom;
import ru.zakharov.roles.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "users")
@Setter
@Getter
@SecondaryTable(name = "users_avatars", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id"))
// DON'T FORGET THIS ANNOTATION IF YOU USE LAZY LOADING!!!
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "enabled")
    private Boolean enabled;

    @NotBlank(message = "username cannot be empty")
    @Column(name = "username")
    private String username;

    @NotBlank(message = "enter the password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "avatar_uri", table = "users_avatars")
    private String avatarURI;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "author",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<WallMessage> wallMessages;

    @OneToMany(mappedBy = "author",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<WallMessageComment> comments;

    @OneToMany(mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Like> allLikes;

    @ManyToMany(mappedBy = "users",
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatRoom> chats = new ArrayList<>();

    @Column(name = "date_of_birth")
    private Timestamp dateOfBirth;

    public User() {
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(int id, String username, Timestamp dateOfBirth) {
        this.id = id;
        this.username = username;
        this.dateOfBirth = dateOfBirth;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        addRole(UserRole.USER);
    }

    public User(String username, String password, String email, Timestamp dateOfBirth, Boolean enabled) {
        this(username, password);
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.enabled = enabled;
    }

    public void addChatRoom(ChatRoom chatRoom) {
        chats.add(chatRoom);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        roles.forEach(role -> {
            Set<SimpleGrantedAuthority> authorities = role.getAuthorities();
            grantedAuthorities.addAll(authorities);
        });
        return grantedAuthorities;
    }

    public void addRole(UserRole role) {
        roles.add(role);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
