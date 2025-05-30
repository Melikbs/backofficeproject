package com.example.backofficeproject.service;

import java.util.List;



import com.example.backofficeproject.Dto.ProduitRequestDto;
import com.example.backofficeproject.Exceptions.ResourceNotFoundException;
import com.example.backofficeproject.mapper.ProduitMapper;
import com.example.backofficeproject.model.Gamme;
import com.example.backofficeproject.model.Marque;
import com.example.backofficeproject.model.Produit;
import com.example.backofficeproject.model.Stock;
import com.example.backofficeproject.repositories.GammeRepository;
import com.example.backofficeproject.repositories.MarqueRepository;
import com.example.backofficeproject.repositories.ProduitRepository;
import com.example.backofficeproject.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final GammeRepository gammeRepository;
    private final MarqueRepository marqueRepository;
    private final ProduitMapper produitMapper;

private final StockRepository stockRepository;
    public Produit createProduit(ProduitRequestDto produitRequestDto) {
        Produit produit = produitMapper.toEntity(produitRequestDto);

        Gamme gamme = gammeRepository.findById(produitRequestDto.getCodeGamme())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Marque marque = marqueRepository.findById(produitRequestDto.getCodeMarque())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        produit.setGamme(gamme);
        produit.setMarque(marque);

        return produitRepository.save(produit);
    }

    public Produit updateProduit(Long id, ProduitRequestDto produitRequestDto) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        produit.setLibelle(produitRequestDto.getLibelle());
        produit.setPrix(produitRequestDto.getPrix());
        produit.setPoids(produitRequestDto.getPoids());
        produit.setDescription(produitRequestDto.getDescription());
        produit.setImage(produitRequestDto.getImage());  // Update the image
        produit.setActif(produitRequestDto.isActif());




        return produitRepository.save(produit);
    }
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
    }

    public void deleteProduit(Long id) {
        produitRepository.deleteById(id);
    }

    public void toggleActivation(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));
        produit.setActif(!produit.isActif());
        produitRepository.save(produit);
    }}

