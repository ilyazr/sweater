package ru.zakharov.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.zakharov.models.User;

public final class PrincipalUtil {
    private PrincipalUtil() {}

    public static User getPrincipalAsUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static int getPrincipalId() {
        return getPrincipalAsUser().getId();
    }

    public static String getPrincipalUsername() {
        return getPrincipalAsUser().getUsername();
    }
}
