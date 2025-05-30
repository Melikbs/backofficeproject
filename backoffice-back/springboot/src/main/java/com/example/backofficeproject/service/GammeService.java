package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.GammeDto;
import com.example.backofficeproject.mapper.GammeMapper;
import com.example.backofficeproject.model.Gamme;
import com.example.backofficeproject.repositories.GammeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GammeService {
    @Autowired
    private GammeRepository gammeRepository;
    @Autowired
    private GammeMapper gammeMapper;

    public List<GammeDto> getAll() {
        return gammeRepository.findAll().stream()
                .map(gammeMapper::toDto)
                .collect(Collectors.toList());
    }

    public GammeDto getById(Long codeGamme) {
        return gammeRepository.findById(codeGamme)
                .map(gammeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Gamme not found"));
    }

    public GammeDto add(GammeDto dto) {
        return gammeMapper.toDto(gammeRepository.save(gammeMapper.toEntity(dto)));
    }

    public GammeDto toggleFlag(Long codeGamme) {
        Gamme gamme = gammeRepository.findById(codeGamme)
                .orElseThrow(() -> new EntityNotFoundException("Gamme not found"));
        gamme.setFlag(!gamme.isFlag());
        gamme = gammeRepository.save(gamme);
        return gammeMapper.toDto(gamme);
    }

    public void deleteGamme(Long codeGamme) {
        gammeRepository.deleteById(codeGamme);
    }
}
