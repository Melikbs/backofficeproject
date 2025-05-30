package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Caracteristique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaracteristiqueRepository extends JpaRepository<Caracteristique, Long> {}
