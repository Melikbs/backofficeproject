package com.example.backofficeproject.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
public class ProduitResponseDto {
    private Long codeProduit;
    private Long codeGamme;
    private Long codeMarque;

    private String libelle;           // Libellé du produit
    private double prix;              // Prix HT
    private double poids;             // Poids
    private String description;       // Description
    private boolean actif;            // Statut actif ou non
    private String libelleGamme;      // Nom de la gamme
    private String libelleMarque;     // Nom de la marque
    private List<String> couleursDisponibles; // Liste des couleurs disponibles (optionnel)
    private String image;             // Image du produit (le chemin ou le nom du fichier)
}
