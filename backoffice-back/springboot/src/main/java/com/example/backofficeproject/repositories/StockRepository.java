package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByProduitCodeProduit(Long codeProduit);

}
