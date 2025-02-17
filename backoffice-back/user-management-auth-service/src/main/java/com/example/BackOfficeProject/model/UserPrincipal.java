package com.example.BackOfficeProject.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private Users user;
    private boolean flag;

    public UserPrincipal(Users user ) {
        this.user = user;
        this.flag = user.isFlag();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            throw new RuntimeException("User has no roles assigned!");
        }
        return user.getRole().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getLabel()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // If you use email as login:
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isFlag(); }
}
