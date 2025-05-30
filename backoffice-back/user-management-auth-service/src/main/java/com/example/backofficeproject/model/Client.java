package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeClient;

    private String username;

    private String nom;
    private String prenom;
    private String cin;
    private String tel;
    private String email;
    private String rue;
    private String ville;
    private String codePostal;

    private boolean actif = true;

}
