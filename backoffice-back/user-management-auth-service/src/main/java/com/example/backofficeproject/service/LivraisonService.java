package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.LivraisonDTO;

import java.util.List;

public interface LivraisonService {
    LivraisonDTO createFromCommande(Long codeCommande);
    List<LivraisonDTO> getAll();
    LivraisonDTO getById(Long id);
    LivraisonDTO validerLivraison(Long id);
    LivraisonDTO getByCommandeCode(Long codeCommande);

}
