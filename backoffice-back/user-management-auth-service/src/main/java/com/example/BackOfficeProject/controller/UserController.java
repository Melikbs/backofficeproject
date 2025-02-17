package com.example.BackOfficeProject.controller;

import com.example.BackOfficeProject.Dto.UserRoleDto;
import com.example.BackOfficeProject.model.Roles;
import com.example.BackOfficeProject.model.Users;
import com.example.BackOfficeProject.repositories.UserRepo;
import com.example.BackOfficeProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserService service;




    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return service.register(user);

    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return service.verify(user);
    }


    @PutMapping("/admin/users/{userId}/role")
    public ResponseEntity<?> assignRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request
    ) {
        String newRole = request.get("role");
        Users updatedUser = service.assignRoleToUser(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserRoleDto>> getAllUsersWithRoles() {
        List<Users> users = userRepo.findAll();
        List<UserRoleDto> dtoList = users.stream()
                .map(user -> new UserRoleDto(
                        user.getUsername(),
                        user.getRole().stream()
                                .map(Roles::getLabel)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }




        // Endpoint pour activer un utilisateur
        @PutMapping("/admin/users/{id}/activate")
        public ResponseEntity<String> activateUser(@PathVariable Long id) {
            Users user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            user.setFlag(true);  // Mettre flag à true pour activer
            userRepo.save(user);
            return ResponseEntity.ok("Utilisateur activé avec succès !");
        }

        // Endpoint pour désactiver un utilisateur
        @PutMapping("/admin/users/{id}/deactivate")
        public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
            Users user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            user.setFlag(false);  // Mettre flag à false pour désactiver
            userRepo.save(user);
            return ResponseEntity.ok("Utilisateur désactivé avec succès !");
        }


    @PutMapping("/admin/users/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable Long userId, @RequestBody Users updatedUser) {
        Users updated = service.updateUser(userId, updatedUser);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }



    @GetMapping("/admin/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }




}






