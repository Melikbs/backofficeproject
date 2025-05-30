package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.LivraisonDTO;
import com.example.backofficeproject.mapper.LivraisonMapper;
import com.example.backofficeproject.model.Commande;
import com.example.backofficeproject.model.Livraison;
import com.example.backofficeproject.model.StatusLivraison;
import com.example.backofficeproject.repositories.CommandeRepository;
import com.example.backofficeproject.repositories.LivraisonRepository;
import com.example.backofficeproject.utilities.ShippingLabelGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LivraisonServiceImpl implements LivraisonService {

    @Autowired
    private LivraisonRepository livraisonRepo;

    @Autowired
    private LivraisonMapper mapper;

    @Autowired
    private CommandeRepository commandeRepo;

    @Override
    public LivraisonDTO createFromCommande(Long codeCommande) {
        Commande commande = commandeRepo.findByIdWithLignesAndProduits(codeCommande)
                .orElseThrow(() -> new RuntimeException("Commande not found"));

        Livraison livraison = new Livraison();
        livraison.setCommande(commande);
        livraison.setDateLivraison(new Date());
        livraison.setStatusLivraison(StatusLivraison.CREE);

        return mapper.toDto(livraisonRepo.save(livraison));
    }



    @Override
    public List<LivraisonDTO> getAll() {
        return livraisonRepo.findAll().stream()
                .filter(l -> l.getCommande() != null) // ✅ ignore corrupted livraison records
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public LivraisonDTO getById(Long id) {
        return livraisonRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public LivraisonDTO getByCommandeCode(Long codeCommande) {
        Livraison livraison = livraisonRepo.findByCommande_CodeCommande(codeCommande)
                .orElseThrow(() -> new RuntimeException("Aucune livraison liée à cette commande"));
        return mapper.toDto(livraison);
    }

    @Autowired
    private ShippingLabelGenerator shippingLabelGenerator;

    @Override
    public LivraisonDTO validerLivraison(Long id) {
        Livraison livraison = livraisonRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Livraison introuvable"));

        if (livraison.getStatusLivraison() != StatusLivraison.CREE) {
            throw new IllegalStateException("La livraison a déjà été validée ou expédiée.");
        }

        String trackingNumber = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        String clientName = livraison.getCommande().getClient().getNom(); // adjust if needed
        String address = "Rue Béchir Khraief, Hammam Chott"; // example static, fetch if dynamic

        String labelUrl;
        try {
            labelUrl = shippingLabelGenerator.generateLabel(trackingNumber, clientName, address);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération de l'étiquette", e);
        }

        livraison.setNumTracking(trackingNumber);
        livraison.setShippingLabel(labelUrl);
        livraison.setStatusLivraison(StatusLivraison.VALIDEE);
        livraison.setDateLivraison(new Date());

        livraisonRepo.save(livraison);
        return mapper.toDto(livraison);
    }


}
