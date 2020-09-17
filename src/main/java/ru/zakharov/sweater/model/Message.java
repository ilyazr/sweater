package ru.zakharov.sweater.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Calendar created_at;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Calendar updated_at;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "msg_text")
    private String text;

    @Column(name = "tag")
    private String tag;

    public Message() {
    }

    public Message(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "id=" + id +
                /*", created_at=" + created_at +
                ", updated_at=" + updated_at +*/
                ", author=" + author.getUsername() +
                ", text='" + text + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }

    public void updateDate() {
        updated_at = Calendar.getInstance();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Calendar created_at) {
        this.created_at = created_at;
    }

    public Calendar getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Calendar updated_at) {
        this.updated_at = updated_at;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
