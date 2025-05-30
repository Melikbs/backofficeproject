package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.ProfileDto;
import com.example.backofficeproject.Dto.UserDto;
import com.example.backofficeproject.Dto.UserRoleDto;
import com.example.backofficeproject.model.MessageResponse;
import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.model.UserProfile;
import com.example.backofficeproject.model.Users;

import com.example.backofficeproject.repositories.UserProfileRepository;
import com.example.backofficeproject.repositories.UserRepo;
import com.example.backofficeproject.service.EmailService;
import com.example.backofficeproject.service.PasswordResetService;
import com.example.backofficeproject.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController

public class UserController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;  // 🔹 Injection correcte du PasswordEncoder

    @Autowired
    private PasswordResetService passwordResetService;

    // 🟢 Enregistrement d'un utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {

        // Check if email already exists
        if (service.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Check if username already exists
        if (service.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already in use!"));
        }

        // Check for strong password
        if (!isPasswordStrong(user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Password is not strong enough!"));
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user
        Users newUser = service.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @PostMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean available = !service.existsByUsername(username);
        return ResponseEntity.ok(Collections.singletonMap("available", available));
    }

    // Check if an email is available
    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean available = !service.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("available", available));
    }

    // Check if the password meets the strength requirements
    private boolean isPasswordStrong(String password) {
        String passwordRegex = "(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}";
        return password.matches(passwordRegex);
    }


    // 🟢 Connexion et génération de token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        String token = service.verify(user);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // 🟢 Assignation de rôle par l'admin
    @PutMapping("/admin/users/{userId}/role")
    public ResponseEntity<Users> assignRole(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String newRole = request.get("role");
        Users updatedUser = service.assignRoleToUser(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    // 🟢 Récupérer tous les utilisateurs avec leur rôle
    @GetMapping("/admin/users/roles")
    public ResponseEntity<List<UserRoleDto>> getAllUsersWithRoles() {
        List<UserRoleDto> dtoList = userRepo.findAll().stream()
                .map(user -> new UserRoleDto(
                        user.getUsername(),
                        user.getRole().stream()
                                .map(Roles::getLabel)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    // 🟢 Activation d'un utilisateur
    @PutMapping("/admin/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        Users user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate role exists
        if (user.getRole().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Assign a role before activation"));
        }

        user.setFlag(true);
        userRepo.save(user);

        // Send email with first role
        String firstRole = user.getRole().iterator().next().getLabel();
        emailService.sendActivationEmail(user.getEmail(), firstRole);

        return ResponseEntity.ok(Collections.singletonMap("message", "User activated and email sent"));
    }
    // 🟢 Désactivation d'un utilisateur
    @PutMapping("/admin/users/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        Users user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setFlag(false);
        userRepo.save(user);
        return ResponseEntity.ok("Utilisateur désactivé avec succès !");
    }

    // 🟢 Mise à jour d'un utilisateur
    @PutMapping("/admin/users/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable Long userId, @RequestBody Users updatedUser) {
        return ResponseEntity.ok(service.updateUser(userId, updatedUser));
    }

    // 🟢 Suppression d'un utilisateur
    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        service.deleteUser(userId);
        return ResponseEntity.ok("Utilisateur supprimé avec succès !");
    }

    // 🟢 Récupérer tous les utilisateurs
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepo.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    // 🟢 Mot de passe oublié
    @PostMapping("/users/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody EmailRequest request) {
        passwordResetService.createPasswordResetToken(request.getEmail());
        return ResponseEntity.ok().body(
                Map.of("message", "Password reset email sent successfully")
        );
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().body(
                Map.of("message", "Password successfully reset")
        );
    }

    // UserController.java
    @GetMapping("/profile")
    public ResponseEntity<ProfileDto> getProfile(Authentication authentication) {
        // Add generic type parameter <Users>
        Optional<Users> userOptional = userRepo.findByUsername(authentication.getName());

        Users user = userOptional.orElseThrow(() ->
                new RuntimeException("User not found"));

        // Get profile from User entity
        UserProfile profile = user.getProfile();

        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }

        ProfileDto dto = new ProfileDto(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getAge(),
                profile.getAddress(),
                profile.getBirthDate()
        );

        return ResponseEntity.ok(dto);
    }
    @Autowired
    private UserProfileRepository userProfileRepository;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody ProfileDto profileDTO,
            Authentication authentication
    ) {
        Optional<Users> userOptional = userRepo.findByUsername(authentication.getName());
        Users user = userOptional.orElseThrow(() ->
                new RuntimeException("User not found"));

        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
        }

        profile.setFirstName(profileDTO.getFirstName());
        profile.setLastName(profileDTO.getLastName());
        profile.setAge(profileDTO.getAge());
        profile.setAddress(profileDTO.getAddress());
        profile.setBirthDate(profileDTO.getBirthDate());

        userProfileRepository.save(profile);
        return ResponseEntity.ok("Profile updated");
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EmailRequest {
        @Email
        @NotBlank
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PasswordResetRequest {
        @NotBlank
        private String token;

        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }



}




