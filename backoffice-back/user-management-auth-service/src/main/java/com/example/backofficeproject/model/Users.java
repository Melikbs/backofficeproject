package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeUser;

    private String username;
    private String email;
    private String password;
    private Boolean flag=false;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL) // "user" refers to UserProfile.user
    private UserProfile profile;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> role = new HashSet<>();


    public void addRole(Roles role) {
        this.role.add(role);
    }
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Users{" +
                "codeUser=" + codeUser +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", Flag=" + flag +
                ", role=" +  role.stream().map(Roles::getLabel).collect(Collectors.toList()) +
                '}';


    }
}
