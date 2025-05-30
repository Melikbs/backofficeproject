package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.ClientDto;
import com.example.backofficeproject.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAll();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateClient(@PathVariable Long id) {
        clientService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/blacklisted")
    public List<ClientDto> getBlacklistedClients() {
        return clientService.getBlacklistedClients();
    }

    @PutMapping("/{codeClient}/reactivate")
    public ResponseEntity<Void> reactivateClient(@PathVariable Long codeClient) {
        clientService.reactivateclient(codeClient); // ✅ Corrected
        return ResponseEntity.ok().build();
    }
    @GetMapping("/acheteurs")
    public List<ClientDto> getAcheteurs() {
        return clientService.getAcheteurs();
    }



}