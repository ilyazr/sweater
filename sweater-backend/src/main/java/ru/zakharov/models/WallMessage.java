package ru.zakharov.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Calendar;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "wall_messages")
// DON'T FORGET THIS ANNOTATION IF YOU USE LAZY LOADING!!!
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WallMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text")
    @NotBlank(message = "text can't be null")
    private String text;

    @Column(name = "img_uri")
    private String imgURI;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Calendar createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Calendar updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    private User author;

    @Transient
    private String authorUsername;

    @Transient
    private int authorId;

    @Transient
    private String authorFirstName;

    @Transient
    private String authorLastName;

    @OneToMany(mappedBy = "wallMessage",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<WallMessageComment> comments;

    @OneToMany(mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Like> likes;

    @Transient
    private int amountOfLikes;

    public WallMessage() {
    }

    public WallMessage(String text) {
        this.text = text;
    }

    public WallMessage(String text, String imgURI) {
        this.text = text;
        this.imgURI = imgURI;
    }

    @PostLoad
    public void onPostLoad() {
        amountOfLikes = likes.size();
        authorUsername = author.getUsername();
        authorId = author.getId();
        authorFirstName = author.getFirstName();
        authorLastName = author.getLastName();
    }
}
