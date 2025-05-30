package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Produit;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @EntityGraph(attributePaths = {"marque", "couleurs"})
    List<Produit> findAll();

    @EntityGraph(attributePaths = {"marque", "couleurs"})
    Optional<Produit> findById(Long id);
}
