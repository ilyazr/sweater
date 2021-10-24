package ru.zakharov.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.zakharov.utils.BeanUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtTokenVerifierFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtTokenVerifierFilter() {
        this.jwtUtil = BeanUtil.getBean(JwtUtil.class);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtUtil.getHeaderName());

        // get access to upload files without logging
        boolean permittedUploadingWithoutLogging = false;
        String requestedURI = request.getRequestURI();
        //TODO: remove it later. Only authenticated users should upload files!
        if (requestedURI.equals("/api/fs/upload") ||
                requestedURI.matches("/api/fs/download/shared/[a-z0-9-_]+.[a-z]{2,6}") ||
                requestedURI.equals("/api/fs/files/shared")) {
            permittedUploadingWithoutLogging = true;
        }

        // if token is missing
        if ((header == null || header.isEmpty()) || !header.startsWith(jwtUtil.getTokenPrefix())) {
            if (permittedUploadingWithoutLogging) {
                // principal in this case is anonymousUser
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter writer = response.getWriter();
                writer.write(new ObjectMapper().writeValueAsString("No JWT token found in request headers"));
                writer.flush();
                writer.close();
                return;
            }
        } else {
            String token = header.split(" ")[1];
            try {
                // create principal for retrieving id and username
                Authentication authentication = jwtUtil.processJwtTokenData(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException exc) {
                throw new IllegalStateException(String.format("Token %s cannot be trusted!", token));
            }

            filterChain.doFilter(request, response);
        }

    }
}
