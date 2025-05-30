package com.example.backofficeproject.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
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
    private boolean actif;
}
