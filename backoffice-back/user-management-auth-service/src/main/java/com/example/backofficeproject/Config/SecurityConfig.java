package com.example.backofficeproject.Config;

import com.example.backofficeproject.Helpers.AutowiringSpringBeanJobFactory;
import com.example.backofficeproject.job.LivraisonStatusUpdateJob;
import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.model.Users;
import com.example.backofficeproject.repositories.RoleRepo;
import com.example.backofficeproject.repositories.UserRepo;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;


@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    // SecurityConfig.java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Activation explicite de la config CORS
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/login", "/register").permitAll()
                        .requestMatchers("/users/forgot-password", "/users/reset-password").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/check-email","check-username").permitAll()
                        .requestMatchers("/admin/**").permitAll()
                        .requestMatchers("/profile").permitAll()
                        .requestMatchers("/api/gammes/**","/api/marques/**").permitAll()
                        .requestMatchers("/api/stocks/**").permitAll()
                        .requestMatchers("api/produits/**").permitAll()
                        .requestMatchers("/api/couleurs/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/packs/**").permitAll()
                        .requestMatchers("/api/clients/**").permitAll()
                        .requestMatchers("api/commandes/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()
                        .requestMatchers("/api/livraisons/**").permitAll()
                        .requestMatchers("/api/chatbot/**").permitAll()
                        .requestMatchers("api/ai/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Configuration
    public class QuartzConfig {

        @Bean
        public JobDetail livraisonStatusJobDetail() {
            return JobBuilder.newJob(LivraisonStatusUpdateJob.class)
                    .withIdentity("livraisonStatusJob")
                    .storeDurably() // No trigger yet → manual launch only
                    .build();
        }

        @Bean
        public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
            AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
            jobFactory.setApplicationContext(applicationContext);
            return jobFactory;
        }

        @Bean
        public SchedulerFactoryBean schedulerFactoryBean(
                JobDetail livraisonStatusJobDetail,
                SpringBeanJobFactory jobFactory
        ) {
            SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
            schedulerFactory.setJobFactory(jobFactory);
            schedulerFactory.setJobDetails(livraisonStatusJobDetail);
            schedulerFactory.setAutoStartup(true); // Let the scheduler run, but no job is triggered until manual
            return schedulerFactory;
        }
    }




    // CorsConfig.java
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true); // Critical for cookies/auth headers

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("http://localhost:4200");
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

            // Create roles if they don't exist
            if (roleRepo.count() == 0) {
                Roles adminRole = new Roles();
                adminRole.setLabel("ROLE_ADMINISTRATOR");
                roleRepo.save(adminRole);

                Roles logistiqueRole = new Roles();
                logistiqueRole.setLabel("ROLE_LOGISTIQUE");
                roleRepo.save(logistiqueRole);

                Roles marketingRole = new Roles();
                marketingRole.setLabel("ROLE_MARKETING");
                roleRepo.save(marketingRole);

                System.out.println("✅ Rôles créés avec succès !");
            }

            // Ensure the admin user is created only if it doesn't exist
            Optional<Users> existingAdmin = userRepo.findByUsername("admin");

            if (existingAdmin.isEmpty()) {  // Use isEmpty() instead of == null
                Users admin = new Users();
                admin.setUsername("admin");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(encoder.encode("adminpassword"));
                admin.setFlag(true); // Admin's flag set to true
                Roles adminRole = roleRepo.findByLabel("ROLE_ADMINISTRATOR")
                        .orElseThrow(() -> new RuntimeException("Role ADMINISTRATOR not found!"));

                admin.addRole(adminRole); // Assign the admin role
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