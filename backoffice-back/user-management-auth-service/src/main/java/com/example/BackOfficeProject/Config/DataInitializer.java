package com.example.BackOfficeProject.Config;

import com.example.BackOfficeProject.model.Roles;
import com.example.BackOfficeProject.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ADMINISTRATOR");
        createRoleIfNotExists("LOGISTIQUE");
        createRoleIfNotExists("MARKETING");
    }

    private void createRoleIfNotExists(String label) {
        roleRepo.findByLabel(label).orElseGet(() -> {
            Roles newRole = new Roles(label);
            return roleRepo.save(newRole);
        });
    }
}