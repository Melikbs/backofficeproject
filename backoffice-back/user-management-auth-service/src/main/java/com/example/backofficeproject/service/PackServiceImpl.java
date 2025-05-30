package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.PackDto;
import com.example.backofficeproject.Dto.PackProduitDto;
import com.example.backofficeproject.mapper.PackMapper;
import com.example.backofficeproject.model.Pack;
import com.example.backofficeproject.model.Produit;
import com.example.backofficeproject.repositories.PackRepository;
import com.example.backofficeproject.repositories.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackServiceImpl implements PackService {

    private final PackRepository packRepository;
    private final ProduitRepository produitRepository;
    private final PackMapper packMapper;

    @Override
    public List<PackDto> getAll() {
        return packRepository.findAll().stream()
                .map(packMapper::toDto)
                .toList();
    }

    @Override
    public PackDto create(PackDto dto) {
        List<Produit> produits = produitRepository.findAllById(
                dto.getProduits().stream().map(PackProduitDto::getCodeProduit).toList()
        );
        Pack pack = packMapper.toEntity(dto, produits);
        return packMapper.toDto(packRepository.save(pack));
    }

    @Override
    public PackDto update(Long id, PackDto dto) {
        Pack existing = packRepository.findById(id).orElseThrow();
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        existing.setActif(dto.isActif());
        existing.getProduits().clear();

        List<Produit> produits = produitRepository.findAllById(
                dto.getProduits().stream().map(PackProduitDto::getCodeProduit).toList()
        );

        Pack updated = packMapper.toEntity(dto, produits);
        updated.setId(id);
        return packMapper.toDto(packRepository.save(updated));
    }

    @Override
    public void disable(Long id) {
        Pack pack = packRepository.findById(id).orElseThrow();
        pack.setActif(false);
        packRepository.save(pack);
    }
    @Override
    public void delete(Long id) {
        Pack pack = packRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pack not found"));

        packRepository.delete(pack);
    }

}

