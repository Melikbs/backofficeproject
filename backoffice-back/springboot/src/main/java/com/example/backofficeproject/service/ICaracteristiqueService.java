package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.CaracteristiqueRequest;
import com.example.backofficeproject.model.Caracteristique;

import java.util.List;

public interface ICaracteristiqueService {
    void ajouterCaracteristiques(Long produitId, List<CaracteristiqueRequest> caracteristiqueRequests);
    List<CaracteristiqueRequest> getCaracteristiquesByProduit(Long produitId);
    void supprimerCaracteristiquesByProduit(Long produitId);
}