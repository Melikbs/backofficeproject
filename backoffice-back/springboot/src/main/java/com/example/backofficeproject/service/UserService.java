package com.example.backofficeproject.service;

import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.model.Users;
import com.example.backofficeproject.repositories.PasswordResetTokenRepo;
import com.example.backofficeproject.repositories.RoleRepo;
import com.example.backofficeproject.repositories.UserRepo;
import com.example.backofficeproject.service.JWT.JWTService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepo repo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public Users register(Users user) {
        user.setPassword(encoder.encode(user.getPassword()));


        Roles defaultRole = roleRepo.findByLabel("LOGISTIQUE")
                .orElseThrow(() -> new RuntimeException("Role LOGISTIQUE not found"));

        user.addRole(defaultRole);
        if ("admin".equals(user.getUsername())) {
            defaultRole = roleRepo.findByLabel("ADMINISTRATOR")
                    .orElseThrow(() -> new RuntimeException("Role ADMINISTRATOR not found"));
            user.setFlag(true); // Set the admin's flag to true
        } else {
            user.setFlag(false); // Set the flag for regular users to false
        }
        return repo.save(user);
    }

    public Users save(Users user) {
        return repo.save(user);
    }

    public String verify(Users user) {
        try {
            System.out.println("Authenticating user: " + user.getEmail());

            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            System.out.println("Authentication successful: " + authentication.isAuthenticated());

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail());
                System.out.println("Generated Token: " + token);
                return token;
            } else {
                System.out.println("Authentication failed");
                return "fail";
            }
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return "fail";
        }
    }




    public Users assignRoleToUser(Long userId, String newRoleLabel) {
        Users user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Roles newRole = roleRepo.findByLabel("ROLE_"+newRoleLabel)
                .orElseThrow(() -> new RuntimeException("Invalid role: " + newRoleLabel));
        user.getRole().clear();
        user.getRole().add(newRole);
        return repo.save(user);
    }

    public Users updateUser(Long userId, Users updatedUser) {
        Users user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(encoder.encode(updatedUser.getPassword()));


        // Save the updated user
        return repo.save(user);
    }
    @Transactional
    public void deleteUser(Long userId) {

        Users user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        passwordResetTokenRepo.deleteByUser(user);
        repo.delete(user);
    }
    public boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }





}




