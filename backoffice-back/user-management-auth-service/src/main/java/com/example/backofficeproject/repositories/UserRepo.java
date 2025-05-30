package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {

    Optional findByUsername(String username);
    Users findByEmail(String email);
    Optional<Users> findById(Long id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

}