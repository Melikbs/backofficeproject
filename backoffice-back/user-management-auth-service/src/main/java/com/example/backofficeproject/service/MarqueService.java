package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.MarqueDto;

import java.util.List;

public interface MarqueService {
    List<MarqueDto> getAll();
    MarqueDto getById(Long id);
    MarqueDto create(MarqueDto dto);
    MarqueDto update(Long id, MarqueDto dto);
    void toggleFlag(Long id);
    void delete(Long id);
}
