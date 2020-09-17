package ru.zakharov.sweater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakharov.sweater.model.Message;

import java.util.List;

public interface MessagesRepo extends JpaRepository<Message, Integer> {
    List<Message> findMessagesByTag(String tag);
    List<Message> findMessagesByTagAndAuthorUsername(String tag, String authorUsername);
    List<Message> findMessagesByAuthorUsername(String username);
}
