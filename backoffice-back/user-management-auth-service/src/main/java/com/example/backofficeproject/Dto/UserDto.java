package com.example.backofficeproject.Dto;

import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.model.Users;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
public class UserDto {
    private Long codeUser;
    private String username;
    private String email;
    private boolean flag;
    private List<String> roles;

    // Constructor using User entity
    public UserDto(Users user) {
        this.codeUser = user.getCodeUser();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.flag = user.isFlag();
        this.roles = user.getRole().stream()
                .map(Roles::getLabel)
                .collect(Collectors.toList());
    }

    // Getters and Setters
}