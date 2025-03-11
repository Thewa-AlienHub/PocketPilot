package org.example.pocketpilot.enums;

public enum UserRole {
    ADMIN(1, "ROLE_ADMIN"),
    PREMIUM_USER(2, "ROLE_PREMIUM_USER"),
    REGULAR_USER(3, "ROLE_REGULAR_USER");

    private final int code;
    private final String roleName;

    UserRole(int code, String roleName) {
        this.code = code;
        this.roleName = roleName;
    }

    public int getCode() {
        return code;
    }

    public String getRoleName() {
        return roleName;
    }

    public static UserRole fromCode(int code) {
        for (UserRole role : UserRole.values()) {
            if (role.getCode() == code) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid role code: " + code);
    }
}
