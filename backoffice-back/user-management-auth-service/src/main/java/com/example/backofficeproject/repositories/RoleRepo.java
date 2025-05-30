package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepo extends JpaRepository<Roles, Long> {
    void deleteByLabelIn(List<String> labels);
    Optional<Roles> findByLabel(String label);
}
