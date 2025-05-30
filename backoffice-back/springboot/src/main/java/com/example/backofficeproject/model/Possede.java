package com.example.backofficeproject.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Possede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String valeurCaracteristique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_theme")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_caracteristique")
    private Caracteristique caracteristique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_produit")
    private Produit produit;
}
