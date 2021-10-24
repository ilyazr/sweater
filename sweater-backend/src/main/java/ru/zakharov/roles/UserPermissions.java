package ru.zakharov.roles;

public enum UserPermissions {

    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    UserPermissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
