package com.example.backofficeproject.Dto;


import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class ProduitRequestDto {
    private Long codeGamme;      // ID de la gamme sélectionnée
    private Long codeMarque;     // ID de la marque sélectionnée
    private String libelle;      // Libellé du produit
    private double prix;         // Prix HT
    private double poids;        // Poids
    private String description;  // Description du produit
    private String image;
    private boolean actif;// Image du produit (par exemple, le chemin ou le nom du fichier)
}
