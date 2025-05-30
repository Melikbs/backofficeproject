package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.CommandeDetailDTO;
import com.example.backofficeproject.Dto.CommandeResponseDTO;
import com.example.backofficeproject.Dto.LigneCommandeDTO;
import com.example.backofficeproject.mapper.CommandeMapper;
import com.example.backofficeproject.model.Commande;
import com.example.backofficeproject.model.CommandeStatus;
import com.example.backofficeproject.repositories.CommandeRepository;
import com.example.backofficeproject.repositories.LigneCommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepo;
    private final LigneCommandeRepository ligneRepo;
    private final CommandeMapper mapper;
    private final LivraisonService livraisonService;
    @Override
    public List<CommandeResponseDTO> getAllCommandes() {
        return commandeRepo.findAll().stream()
                .map(mapper::toCommandeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CommandeDetailDTO getCommandeDetails(Long id) {
        Commande commande = commandeRepo.findByIdWithLignesAndProduits(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        List<LigneCommandeDTO> lignes = ligneRepo.findByCommande_CodeCommande(id).stream()
                .map(mapper::toLigneDTO)
                .collect(Collectors.toList());

        return mapper.toDetailDTO(commande, lignes);
    }

    @Override
    public void validateCommande(Long id) {
        Commande commande = commandeRepo.findByIdWithLignesAndProduits(id)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        commande.setStatus(CommandeStatus.VALIDATED);
        commande.setDateValidation(new Date());
        commandeRepo.save(commande);

        livraisonService.createFromCommande(id);
    }


    @Override
    public void refuseCommande(Long id) {
        Commande commande = commandeRepo.findById(id).orElseThrow(() -> new RuntimeException("Commande not found"));
        commande.setStatus(CommandeStatus.REFUSED);
        commandeRepo.save(commande);
    }
}
