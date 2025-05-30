package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.PackDto;

import java.util.List;

public interface PackService {
    List<PackDto> getAll();
    PackDto create(PackDto dto);
    PackDto update(Long id, PackDto dto);
    void disable(Long id);
    void delete(Long id);

}
