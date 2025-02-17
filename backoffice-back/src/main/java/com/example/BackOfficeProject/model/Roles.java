package com.example.BackOfficeProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@Entity
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeRole;


    private String label;
    @ManyToMany(mappedBy = "role")
    @JsonIgnore
    private Set<Users> users = new HashSet<>();

    public Roles() {
    }

    public Roles(String label) {
        this.label = label;
    }


}
