package com.example.backofficeproject.Dto;

import java.util.List;

public class UserRoleDto {
    private String username;
    private List<String> roles;

    public UserRoleDto(String username, List<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
