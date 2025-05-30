package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.CouleurDto;
import com.example.backofficeproject.mapper.CouleurMapper;
import com.example.backofficeproject.model.Couleur;
import com.example.backofficeproject.repositories.CouleurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouleurServiceImpl implements CouleurService {

    private final CouleurRepository couleurRepository;
    private final CouleurMapper couleurMapper;

    @Override
    public List<CouleurDto> getAll() {
        return couleurMapper.toDtoList(couleurRepository.findAll());
    }

    @Override
    public CouleurDto getById(Long id) {
        return couleurMapper.toDto(
                couleurRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Couleur introuvable"))
        );
    }

    @Override
    public CouleurDto create(CouleurDto dto) {
        Couleur couleur = couleurMapper.toEntity(dto);
        return couleurMapper.toDto(couleurRepository.save(couleur));
    }

    @Override
    public CouleurDto update(Long id, CouleurDto dto) {
        Couleur couleur = couleurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Couleur introuvable"));

        couleur.setLibelle(dto.getLibelle());

        return couleurMapper.toDto(couleurRepository.save(couleur));
    }

    @Override
    public void delete(Long id) {
        couleurRepository.deleteById(id);
    }
}
