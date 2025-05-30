package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "user_profiles")
@NoArgsConstructor // Add this
@AllArgsConstructor
public class UserProfile {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
    private LocalDate birthDate;

    @OneToOne
    @JoinColumn(name = "codeUser")
    private Users user;



}