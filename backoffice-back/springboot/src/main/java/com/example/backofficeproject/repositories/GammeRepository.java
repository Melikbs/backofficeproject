package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Gamme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GammeRepository extends JpaRepository<Gamme, Long> {
}