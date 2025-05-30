package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.ProduitRequestDto;
import com.example.backofficeproject.Dto.ProduitResponseDto;
import com.example.backofficeproject.model.Couleur;
import com.example.backofficeproject.model.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import java.util.stream.Collectors;

@Component
public class ProduitMapper {

    public Produit toEntity(ProduitRequestDto dto) {
        Produit produit = new Produit();
        produit.setLibelle(dto.getLibelle());
        produit.setPrix(dto.getPrix());
        produit.setPoids(dto.getPoids());
        produit.setDescription(dto.getDescription());
        produit.setActif(true); // Par défaut, un produit ajouté est actif
        produit.setImage(dto.getImage()); // Assurez-vous que l'image est définie

        return produit;
    }

    public ProduitResponseDto toResponseDto(Produit produit) {
        ProduitResponseDto dto = new ProduitResponseDto();
        dto.setCodeProduit(produit.getCodeProduit());
        dto.setLibelle(produit.getLibelle());
        dto.setPrix(produit.getPrix());
        dto.setPoids(produit.getPoids());
        dto.setDescription(produit.getDescription());
        dto.setActif(produit.isActif());
        dto.setLibelleGamme(produit.getGamme() != null ? produit.getGamme().getLibelle() : null);
        dto.setLibelleMarque(produit.getMarque() != null ? produit.getMarque().getLibelle() : null);
        dto.setImage(produit.getImage()); // Ajouter l'image au DTO
        dto.setCodeGamme(produit.getGamme() != null ? produit.getGamme().getCodeGamme() : null);
        dto.setCodeMarque(produit.getMarque() != null ? produit.getMarque().getCodeMarque() : null);

        if (produit.getStocks() != null) {
            dto.setCouleursDisponibles(
                    produit.getStocks().stream()
                            .map(stock -> stock.getCouleur().getLibelle())
                            .distinct()
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }
}

