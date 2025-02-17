package com.example.BackOfficeProject.Config;

import com.example.BackOfficeProject.model.Roles;
import com.example.BackOfficeProject.model.Users;
import com.example.BackOfficeProject.repositories.RoleRepo;
import com.example.BackOfficeProject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(customizer -> customizer.disable()).
                authorizeHttpRequests(request -> request
                        .requestMatchers("/login", "/register").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMINISTRATOR")
                        .anyRequest().authenticated()).
                httpBasic(Customizer.withDefaults()).
                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);


        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }

    @Bean
    CommandLineRunner initRolesAndAdmin(RoleRepo roleRepo, UserRepo userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            // Vérifier si les rôles existent sinon les créer
            if (roleRepo.count() == 0) {
                Roles adminRole = new Roles();
                adminRole.setLabel("ADMINISTRATOR");
                roleRepo.save(adminRole);

                Roles logistiqueRole = new Roles();
                logistiqueRole.setLabel("LOGISTIQUE");
                roleRepo.save(logistiqueRole);

                Roles marketingRole = new Roles();
                marketingRole.setLabel("MARKETING");
                roleRepo.save(marketingRole);

                System.out.println("✅ Rôles créés avec succès !");
            }

            // Vérifier si l'admin existe sinon le créer
            if (userRepo.findByUsername("admin") == null) {
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(encoder.encode("adminpassword"));

                Roles adminRole = roleRepo.findByLabel("ADMINISTRATOR")
                        .orElseThrow(() -> new RuntimeException("Role ADMINISTRATOR not found!"));

                admin.addRole(adminRole); // Assigner le rôle

                userRepo.save(admin);

                System.out.println("✅ Admin créé avec succès !");
            }
        };
    }



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength factor = 12
    }


}