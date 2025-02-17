package com.example.BackOfficeProject.service;

import com.example.BackOfficeProject.model.Roles;
import com.example.BackOfficeProject.model.Users;
import com.example.BackOfficeProject.repositories.RoleRepo;
import com.example.BackOfficeProject.repositories.UserRepo;
import com.example.BackOfficeProject.service.JWT.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

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
        user.setFlag(true);
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
        Roles newRole = roleRepo.findByLabel(newRoleLabel)
                .orElseThrow(() -> new RuntimeException("Invalid role: " + newRoleLabel));
        user.getRole().clear();
        user.getRole().add(newRole);
        return repo.save(user);
    }

    public Users updateUser(Long userId, Users updatedUser) {
        Users user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user fields (for example, username and email)
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setPassword(encoder.encode(updatedUser.getPassword())); // Only update password if needed


        // Save the updated user
        return repo.save(user);
    }

    public void deleteUser(Long userId) {
        Users user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        repo.delete(user);
    }




}
