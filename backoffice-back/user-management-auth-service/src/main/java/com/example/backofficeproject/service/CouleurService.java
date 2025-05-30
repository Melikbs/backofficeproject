package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.CouleurDto;

import java.util.List;

public interface CouleurService {
    List<CouleurDto> getAll();
    CouleurDto getById(Long id);
    CouleurDto create(CouleurDto dto);
    CouleurDto update(Long id, CouleurDto dto);
    void delete(Long id);
}