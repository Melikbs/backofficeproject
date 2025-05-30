package com.example.backofficeproject.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LigneCommandeDTO {
    private String libelleProduit;
    private double prix;
    private int quantite;
    private double poids;       // ✅ Add this

}
