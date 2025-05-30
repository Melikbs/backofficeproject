package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Possede;
import com.example.backofficeproject.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PossedeRepository extends JpaRepository<Possede, Long> {
    List<Possede> findAllByProduit(Produit produit);
}
