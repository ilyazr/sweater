package ru.zakharov.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.zakharov.models.User;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Getter
public class JwtUtil {

    private final String secretKey = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret";
    private final long expiresAfterDays = 7L;
    private final String headerName = "Authorization";
    private final String tokenPrefix = "Bearer ";
    private final ObjectMapper mapper = JsonMapper.builder().build();


    public Jws<Claims> parseJwt(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token);
    }

    @SuppressWarnings("unchecked")
    public UsernamePasswordAuthenticationToken processJwtTokenData(String token) throws JwtException {
        Jws<Claims> claimsJws = parseJwt(token);
        Claims body = claimsJws.getBody();
        int id = (int) body.get("id");
        String username = body.getSubject();
        var authorities = (List<Map<String, String>>) body.get("authorities");

        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                .map(s -> new SimpleGrantedAuthority(s.get("authority")))
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(
                new User(id, username),
                null,
                simpleGrantedAuthorities
        );
    }

    public String createJwtToken(int userId, String username, Collection<? extends GrantedAuthority> authorities) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("authorities", authorities)
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(getExpirationDate())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String createJwtTokenWithBasicPrefix(int userId,
                                                String username,
                                                Collection<? extends GrantedAuthority> authorities) {
        return tokenPrefix + createJwtToken(userId, username, authorities);
    }

    private Date getExpirationDate() {
        return Date.valueOf(LocalDate.now().plusDays(expiresAfterDays));
    }

    public <T> T readValueFromRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        return mapper.readValue(request.getInputStream(), clazz);
    }
}
