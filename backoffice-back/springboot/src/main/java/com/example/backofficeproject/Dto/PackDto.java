package com.example.backofficeproject.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class PackDto {
    private Long id;
    private String nom;
    private String description;
    private boolean actif;
    private List<PackProduitDto> produits;
}
