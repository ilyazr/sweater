package ru.zakharov.sweater;

import org.springframework.security.core.GrantedAuthority;

public enum Roles implements GrantedAuthority {
    USER ("Пользователь"),
    ADMIN ("Администратор");

    private String description;

    Roles(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
