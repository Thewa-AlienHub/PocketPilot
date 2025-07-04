package org.example.pocketpilot.utils;

import org.bson.types.ObjectId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final ObjectId userId;
    private final String username;
    private final String role;
    private final String userEmail;

    public CustomUserDetails(ObjectId userId, String username, String role , String userEmail) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.userEmail = userEmail;
    }

    public ObjectId getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role);
    }

    @Override
    public String getPassword() {
        return null; // Password is not stored here
    }

    @Override
    public String getUsername() {
        return username;
    }



    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}