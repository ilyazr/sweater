package ru.zakharov.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import ru.zakharov.jwt.JwtUtil;
import ru.zakharov.utils.BeanUtil;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class StompHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public StompHandshakeInterceptor() {
        this.jwtUtil = BeanUtil.getBean(JwtUtil.class);
    }

    private void readAllHeadersOfRequest(HttpHeaders headers) {
        headers.forEach((k, v) -> {
            System.out.println(k+": " + v.stream().collect(Collectors.joining(", ")));
        });
    }

    private void checkForToken(ServerHttpRequest req) {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) req;
        String rawQuery = servletRequest.getURI().getRawQuery();
        log.info(rawQuery);
        Jws<Claims> claims = null;
        try {
            claims = jwtUtil.parseJwt(rawQuery.split("=")[1]);
        } catch (JwtException e) {
            log.error("JWT parse error");
            throw new RuntimeException(e);
        }
        Claims body = claims.getBody();
        int id = (int) body.get("id");
        String username = body.getSubject();
        System.out.printf("ID: %d\nUSERNAME: %s\n", id, username);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest req,
                                   ServerHttpResponse resp,
                                   WebSocketHandler webSocketHandler,
                                   Map<String, Object> map) throws Exception {
//        remove it
//        readAllHeadersOfRequest(req.getHeaders());
//        checkForToken(req);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler,
                               Exception e) {

    }
}
