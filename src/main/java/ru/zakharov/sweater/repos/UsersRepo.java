package ru.zakharov.sweater.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakharov.sweater.model.Message;
import ru.zakharov.sweater.model.User;

import java.util.List;

public interface UsersRepo extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
