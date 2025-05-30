package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.StockDto;

import java.util.List;

public interface StockService {
    List<StockDto> getAll();
    StockDto getById(Long id);
    StockDto create(StockDto dto);
    StockDto update(Long id, StockDto dto);
    List<StockDto> getStocksByProduitId(Long produitId);

    void delete(Long id);
}
