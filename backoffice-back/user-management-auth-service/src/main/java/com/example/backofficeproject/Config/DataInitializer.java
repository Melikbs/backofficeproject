package com.example.backofficeproject.Config;

import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public void run(String... args) {
        createRoleIfNotExists("ROLE_ADMINISTRATOR");
        createRoleIfNotExists("ROLE_LOGISTIQUE");
        createRoleIfNotExists("ROLE_MARKETING");

        // Clean up legacy roles
        roleRepo.deleteByLabelIn(Arrays.asList("ADMINISTRATOR", "LOGISTIQUE", "MARKETING"));
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepo.findByLabel(roleName).orElseGet(() -> {
            Roles newRole = new Roles();
            newRole.setLabel(roleName);
            return roleRepo.save(newRole);
        });
    }
}