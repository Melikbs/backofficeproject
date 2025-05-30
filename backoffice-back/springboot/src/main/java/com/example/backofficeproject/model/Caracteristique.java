package com.example.backofficeproject.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Caracteristique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeCaracteristique;

    private String libelle;

    @OneToMany(mappedBy = "caracteristique", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Possede> possedes = new ArrayList<>();
}