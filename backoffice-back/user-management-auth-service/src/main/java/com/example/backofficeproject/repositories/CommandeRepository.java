package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @Query("SELECT c FROM Commande c LEFT JOIN FETCH c.lignes l LEFT JOIN FETCH l.produit WHERE c.codeCommande = :codeCommande")
    Optional<Commande> findByIdWithLignesAndProduits(@Param("codeCommande") Long codeCommande);
}
