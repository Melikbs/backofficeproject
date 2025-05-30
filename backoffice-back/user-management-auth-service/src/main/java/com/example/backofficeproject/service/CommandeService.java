package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.CommandeDetailDTO;
import com.example.backofficeproject.Dto.CommandeResponseDTO;

import java.util.List;

public interface CommandeService {
    List<CommandeResponseDTO> getAllCommandes();
    CommandeDetailDTO getCommandeDetails(Long id);
    void validateCommande(Long id);
    void refuseCommande(Long id);
}

