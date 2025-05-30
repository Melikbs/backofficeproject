package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.MarqueDto;
import com.example.backofficeproject.mapper.MarqueMapper;
import com.example.backofficeproject.model.Gamme;
import com.example.backofficeproject.model.Marque;
import com.example.backofficeproject.repositories.GammeRepository;
import com.example.backofficeproject.repositories.MarqueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarqueServiceImpl implements MarqueService {

    private final MarqueRepository marqueRepository;
    private final MarqueMapper marqueMapper;
    private final GammeRepository gammeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<MarqueDto> getAll() {
        return marqueRepository.findAll().stream()
                .map(marqueMapper::toDto)
                .toList();
    }

    @Override
    public MarqueDto getById(Long id) {
        Marque marque = marqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marque not found"));
        return marqueMapper.toDto(marque);
    }

    @Override
    public MarqueDto create(MarqueDto dto) {
        Gamme gamme = gammeRepository.findById(dto.getCodeGamme())
                .orElseThrow(() -> new EntityNotFoundException("Gamme with id " + dto.getCodeGamme() + " not found"));

        Marque marque = Marque.builder()
                .libelle(dto.getLibelle())
                .logo(dto.getLogo())
                .actif(dto.getActif())
                .gamme(gamme)
                .build();

        marque = marqueRepository.save(marque);

        return marqueMapper.toDto(marque);
    }


    @Override
    public MarqueDto update(Long id, MarqueDto dto) {
        Marque marque = marqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marque not found"));

        marque.setLibelle(dto.getLibelle());
        marque.setLogo(dto.getLogo());
        if (dto.getActif() != null) marque.setActif(dto.getActif());

        if (dto.getCodeGamme() != null) {
            Gamme gamme = gammeRepository.findById(dto.getCodeGamme())
                    .orElseThrow(() -> new RuntimeException("Gamme with id " + dto.getCodeGamme() + " not found"));
            marque.setGamme(gamme);
        }

        return marqueMapper.toDto(marqueRepository.save(marque));
    }

    @Override
    public void delete(Long id) {
        marqueRepository.deleteById(id);
    }

    public void toggleFlag(Long codeMarque) {
        Marque marque = marqueRepository.findById(codeMarque)
                .orElseThrow(() -> new EntityNotFoundException("Marque not found"));
        marque.setActif(!marque.isActif()); // Active <=> Inactive
        marqueRepository.save(marque);
    }

}
