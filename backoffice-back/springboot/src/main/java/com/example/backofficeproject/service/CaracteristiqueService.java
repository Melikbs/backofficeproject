package com.example.backofficeproject.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.backofficeproject.Dto.CaracteristiqueRequest;
import com.example.backofficeproject.model.Caracteristique;
import com.example.backofficeproject.model.Possede;
import com.example.backofficeproject.model.Produit;
import com.example.backofficeproject.model.Theme;
import com.example.backofficeproject.repositories.CaracteristiqueRepository;
import com.example.backofficeproject.repositories.PossedeRepository;
import com.example.backofficeproject.repositories.ProduitRepository;
import com.example.backofficeproject.repositories.ThemeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CaracteristiqueService implements ICaracteristiqueService {

    private final ProduitRepository produitRepository;
    private final ThemeRepository themeRepository;
    private final CaracteristiqueRepository caracteristiqueRepository;
    private final PossedeRepository possedeRepository;

    @Override
    @Transactional
    public void ajouterCaracteristiques(Long produitId, List<CaracteristiqueRequest> caracteristiqueRequests) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        for (CaracteristiqueRequest req : caracteristiqueRequests) {
            Theme theme = themeRepository.findByLibelle(req.getTheme())
                    .orElseGet(() -> {
                        Theme newTheme = new Theme();
                        newTheme.setLibelle(req.getTheme());
                        newTheme.setActif(true);
                        return themeRepository.save(newTheme);
                    });

            Caracteristique caract = new Caracteristique();
            caract.setLibelle(req.getValeur());
            caracteristiqueRepository.save(caract);

            Possede possede = new Possede();
            possede.setProduit(produit);
            possede.setTheme(theme);
            possede.setCaracteristique(caract);
            possede.setValeurCaracteristique(req.getValeur());

            possedeRepository.save(possede);
        }
    }

    @Override
    public List<CaracteristiqueRequest> getCaracteristiquesByProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        List<Possede> possessions = possedeRepository.findAllByProduit(produit);
        List<CaracteristiqueRequest> responses = new ArrayList<>();

        for (Possede p : possessions) {
            responses.add(new CaracteristiqueRequest(
                    p.getValeurCaracteristique(),
                    p.getTheme().getLibelle()
            ));
        }

        return responses;
    }

    @Override
    @Transactional
    public void supprimerCaracteristiquesByProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        List<Possede> possessions = possedeRepository.findAllByProduit(produit);
        possedeRepository.deleteAll(possessions);
    }
}
