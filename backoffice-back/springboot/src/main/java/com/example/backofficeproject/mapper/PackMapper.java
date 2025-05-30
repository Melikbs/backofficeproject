package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.PackDto;
import com.example.backofficeproject.Dto.PackProduitDto;
import com.example.backofficeproject.model.Pack;
import com.example.backofficeproject.model.PackProduit;
import com.example.backofficeproject.model.Produit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PackMapper {

    public Pack toEntity(PackDto dto, List<Produit> produits) {
        // Vérifier si dto.getProduits() est nul AVANT de créer le pack
        List<PackProduit> packProduits = new ArrayList<>();

        Pack pack = Pack.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .actif(dto.isActif())
                .produits(packProduits) // assigné ici vide, sera rempli ensuite
                .build();

        if (dto.getProduits() != null) {
            for (PackProduitDto pDto : dto.getProduits()) {
                Produit produit = produits.stream()
                        .filter(p -> p.getCodeProduit().equals(pDto.getCodeProduit()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Produit introuvable avec l'ID: " + pDto.getCodeProduit()));

                PackProduit packProduit = PackProduit.builder()
                        .produit(produit)
                        .pack(pack)
                        .reductionPourcentage(pDto.getReductionPourcentage())
                        .reductionValeur(pDto.getReductionValeur())
                        .build();

                packProduits.add(packProduit);
            }
        }

        return pack;
    }

    public PackDto toDto(Pack pack) {
        List<PackProduitDto> produits = new ArrayList<>();

        if (pack.getProduits() != null) {
            produits = pack.getProduits().stream()
                    .map(pp -> {
                        PackProduitDto dto = new PackProduitDto();
                        dto.setCodeProduit(pp.getProduit().getCodeProduit());
                        dto.setReductionPourcentage(pp.getReductionPourcentage());
                        dto.setReductionValeur(pp.getReductionValeur());
                        return dto;
                    })
                    .toList();
        }

        return PackDto.builder()
                .id(pack.getId())
                .nom(pack.getNom())
                .description(pack.getDescription())
                .actif(pack.isActif())
                .produits(produits)
                .build();
    }
}
