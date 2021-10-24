package ru.zakharov.listeners;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import ru.zakharov.jwt.JwtUtil;
import ru.zakharov.models.OnlineUser;
import ru.zakharov.services.OnlineUserService;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketListener {

    private final OnlineUserService onlineUserService;
    private final JwtUtil jwtUtil;

    @EventListener
    public void onSessionConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(event.getMessage());
        List<String> tokenHeader = stompAccessor.getNativeHeader("token");
        if (tokenHeader == null || tokenHeader.size() < 1) {
            throw new RuntimeException("Missing JWT token");
        } else {
            try {
                String authHeader = tokenHeader.get(0);
                String token = authHeader.split(" ")[1];
                Claims body = jwtUtil.parseJwt(token).getBody();
                int id = (int) body.get("id");
                String username = body.getSubject();
                String sessionId = stompAccessor.getSessionId();
                OnlineUser onlineUser = new OnlineUser(id, username, sessionId);
                onlineUserService.addOnlineUser(onlineUser);
            } catch (JwtException e) {
                log.error("JWT parse error");
                throw new RuntimeException(e);
            }
        }
    }

    @EventListener
    public void onSessionDisconnectedEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = stompAccessor.getSessionId();
        onlineUserService.removeOnlineUser(sessionId);
    }

}
