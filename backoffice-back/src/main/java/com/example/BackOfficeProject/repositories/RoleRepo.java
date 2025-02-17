package com.example.BackOfficeProject.repositories;

import com.example.BackOfficeProject.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Roles, Long> {

    Optional<Roles> findByLabel(String label);
}
