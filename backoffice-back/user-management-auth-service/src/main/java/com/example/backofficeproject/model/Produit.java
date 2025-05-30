package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeProduit; // harmonisé
    private String libelle; // harmonisé
    private String description;
    private Double prix; // harmonisé
    private Double poids;
    private String image;
    private boolean actif; // harmonisé

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_marque")
    private Marque marque;
    @ManyToOne
    @JoinColumn(name = "codeGamme")
    private Gamme gamme;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "produit_couleur",
            joinColumns = @JoinColumn(name = "code_produit"), // ici on met le vrai nom
            inverseJoinColumns = @JoinColumn(name = "code_couleur")) // ici aussi

    private Set<Couleur> couleurs;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL)
    private List<Stock> stocks; // CORRECTION
}
