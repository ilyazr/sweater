package ru.zakharov.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.zakharov.models.User;
import ru.zakharov.utils.BeanUtil;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = BeanUtil.getBean(JwtUtil.class);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //TODO: Create custom response for empty body (filter-related thing)
        try {
            UsernameAndPasswordRequest usernameAndPasswordRequest =
                    jwtUtil.readValueFromRequest(request, UsernameAndPasswordRequest.class);

            log.info("Authentication attempt as user named {}", usernameAndPasswordRequest.getUsername());

            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    usernameAndPasswordRequest.getUsername(),
                    usernameAndPasswordRequest.getPassword()
            );

            // if UserDetails.enabled == false, Spring won't authenticate this user
            return authenticationManager.authenticate(authRequest);

        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        log.info("Successful authentication as user named {}", authResult.getName());
        User principal = ((User) authResult.getPrincipal());
        String token = jwtUtil.createJwtToken(
                principal.getId(),
                authResult.getName(),
                authResult.getAuthorities()
        );
        response.addHeader(jwtUtil.getHeaderName(), jwtUtil.getTokenPrefix() + token);
    }
}
