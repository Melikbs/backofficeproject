package com.example.backofficeproject.service;

import com.example.backofficeproject.model.UserPrincipal;
import com.example.backofficeproject.model.Users;
import com.example.backofficeproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Looking for user with email: " + email);
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("User not found with email: " + email);
            throw new UsernameNotFoundException("User not found");
        }
        if (!user.isFlag()) {
            throw new DisabledException("Compte désactivé, contactez l'admin.");
        }
        System.out.println("User found: " + user);
        return new UserPrincipal(user);
    }
}
