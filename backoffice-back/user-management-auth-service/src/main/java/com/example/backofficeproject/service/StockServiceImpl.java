package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.StockDto;
import com.example.backofficeproject.mapper.StockMapper;
import com.example.backofficeproject.model.Couleur;
import com.example.backofficeproject.model.Produit;
import com.example.backofficeproject.model.Stock;
import com.example.backofficeproject.repositories.CouleurRepository;
import com.example.backofficeproject.repositories.ProduitRepository;
import com.example.backofficeproject.repositories.StockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProduitRepository produitRepository;
    private final CouleurRepository couleurRepository;
    private final StockMapper stockMapper;

    @Override
    public List<StockDto> getAll() {
        return stockMapper.toDtoList(stockRepository.findAll());
    }

    @Override
    public StockDto getById(Long id) {
        return stockMapper.toDto(stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock introuvable")));
    }
    @Transactional
    @Override
    public StockDto create(StockDto dto) {
        Stock stock = stockMapper.toEntity(dto);

        if (dto.getCodeProduit() != null) {
            Produit produit = produitRepository.findById(dto.getCodeProduit())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));
            stock.setProduit(produit);
        }

        if (dto.getCodeCouleur() != null) {
            Couleur couleur = couleurRepository.findById(dto.getCodeCouleur())
                    .orElseThrow(() -> new RuntimeException("Couleur introuvable"));
            stock.setCouleur(couleur);
        }

        return stockMapper.toDto(stockRepository.save(stock));  // Utilisation de save pour ajouter un stock
    }

    @Override
    public List<StockDto> getStocksByProduitId(Long produitId) {
        List<Stock> stocks = stockRepository.findByProduitCodeProduit(produitId);
        return stockMapper.toDtoList(stocks);
    }
@Transactional
    @Override
    public StockDto update(Long id, StockDto dto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock introuvable"));

        stock.setNombreStock(dto.getNombreStock());
        stock.setInStock(dto.getInStock());

        if (dto.getCodeProduit() != null) {
            Produit produit = produitRepository.findById(dto.getCodeProduit())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable"));
            stock.setProduit(produit);
        }

        if (dto.getCodeCouleur() != null) {
            Couleur couleur = couleurRepository.findById(dto.getCodeCouleur())
                    .orElseThrow(() -> new RuntimeException("Couleur introuvable"));
            stock.setCouleur(couleur);
        }

        return stockMapper.toDto(stockRepository.save(stock));
    }


    @Override
    public void delete(Long id) {
        stockRepository.deleteById(id);
    }
}
