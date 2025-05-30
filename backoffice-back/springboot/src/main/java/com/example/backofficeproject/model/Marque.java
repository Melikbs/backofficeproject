package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeMarque;

    private String libelle;

    private String logo;

    @Column(name = "actif")
    private boolean actif;

    @ManyToOne
    @JoinColumn(name = "code_gamme")
    private Gamme gamme;

    @OneToMany(mappedBy = "marque")
    private List<Produit> produits;
}
