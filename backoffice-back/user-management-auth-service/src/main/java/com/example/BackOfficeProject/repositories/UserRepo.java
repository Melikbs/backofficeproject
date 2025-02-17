package com.example.BackOfficeProject.repositories;

import com.example.BackOfficeProject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<Users, Long> {

    Users findByUsername(String username);
    Users findByEmail(String email);
    Optional<Users> findById(Long id);
}