package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.StockDto;
import com.example.backofficeproject.model.Stock;
import com.example.backofficeproject.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public List<StockDto> getAll() {
        return stockService.getAll();
    }

    @GetMapping("/produit/{produitId}")
    public List<StockDto> getStocksByProduit(@PathVariable Long produitId) {
        return stockService.getStocksByProduitId(produitId);
    }

    @GetMapping("/{id}")
    public StockDto get(@PathVariable Long id) {
        return stockService.getById(id);
    }

    @PostMapping
    public ResponseEntity<StockDto> addStock(@RequestBody StockDto dto) {
        try {
            StockDto createdStock = stockService.create(dto);  // Utiliser la méthode create du service
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
        } catch (Exception e) {
            // Log l'erreur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public StockDto update(@PathVariable Long id, @RequestBody StockDto dto) {
        return stockService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        stockService.delete(id);
        return ResponseEntity.ok("Stock supprimé avec succès");
    }
}
