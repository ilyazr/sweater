package ru.zakharov.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.zakharov.models.OnlineUser;
import ru.zakharov.repos.OnlineUserRepo;

@Service
@AllArgsConstructor
@Slf4j
public class OnlineUserService {

    private OnlineUserRepo onlineUserRepo;

    public void addOnlineUser(OnlineUser onlineUser) {
        OnlineUser saved = onlineUserRepo.save(onlineUser);
        log.info("{} connected", saved.getUsername());
    }

    public void removeOnlineUser(String sessionId) {
        OnlineUser onlineUser = onlineUserRepo
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("No user with such session id"));
        onlineUserRepo.delete(onlineUser);
        log.info("{} disconnected", onlineUser.getUsername());
    }

    public boolean isUserOnline(int id) {
        return onlineUserRepo.existsByUserId(id);
    }

}
