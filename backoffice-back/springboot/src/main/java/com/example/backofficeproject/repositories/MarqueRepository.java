package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Marque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarqueRepository extends JpaRepository<Marque, Long> {

    @EntityGraph(attributePaths = {"gamme"})
    List<Marque> findAll();

    @EntityGraph(attributePaths = {"gamme"})
    Optional<Marque> findById(Long id);
}