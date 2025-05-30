package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    Optional<Livraison> findByCommande_CodeCommande(Long codeCommande);
}
