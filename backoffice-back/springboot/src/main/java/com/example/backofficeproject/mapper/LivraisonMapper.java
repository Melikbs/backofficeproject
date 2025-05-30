package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.LivraisonDTO;
import com.example.backofficeproject.model.Livraison;
import com.example.backofficeproject.repositories.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LivraisonMapper {

    @Autowired
    private CommandeRepository commandeRepository;

    public LivraisonDTO toDto(Livraison livraison) {
        LivraisonDTO dto = new LivraisonDTO();
        dto.setCodeLivraison(livraison.getCodeLivraison());
        dto.setNumTracking(livraison.getNumTracking());
        dto.setDateLivraison(livraison.getDateLivraison());
        dto.setShippingLabel(livraison.getShippingLabel());
        dto.setStatusLivraison(livraison.getStatusLivraison());
        dto.setCodeCommande(livraison.getCommande().getCodeCommande());
        return dto;
    }

    public Livraison toEntity(LivraisonDTO dto) {
        Livraison livraison = new Livraison();
        livraison.setCodeLivraison(dto.getCodeLivraison());
        livraison.setNumTracking(dto.getNumTracking());
        livraison.setDateLivraison(dto.getDateLivraison());
        livraison.setShippingLabel(dto.getShippingLabel());
        livraison.setStatusLivraison(dto.getStatusLivraison());
        livraison.setCommande(commandeRepository.findById(dto.getCodeCommande()).orElse(null));
        return livraison;
    }
}
