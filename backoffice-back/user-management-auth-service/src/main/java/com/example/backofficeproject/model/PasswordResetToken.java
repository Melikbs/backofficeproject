package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor

public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expiryDate;

    public PasswordResetToken(Users user,String token,Date expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }
    @Column(nullable = false)
    private boolean used = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "codeUser", nullable = false)
    private Users user;

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }
}
