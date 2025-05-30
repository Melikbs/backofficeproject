package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.CommandeDetailDTO;
import com.example.backofficeproject.Dto.CommandeResponseDTO;
import com.example.backofficeproject.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @GetMapping
    public List<CommandeResponseDTO> getAll() {
        return commandeService.getAllCommandes();
    }

    @GetMapping("/{id}")
    public CommandeDetailDTO getDetails(@PathVariable Long id) {
        return commandeService.getCommandeDetails(id);
    }

    @PutMapping("/{id}/validate")
    public ResponseEntity<?> validate(@PathVariable Long id) {
        commandeService.validateCommande(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/refuse")
    public ResponseEntity<?> refuse(@PathVariable Long id) {
        commandeService.refuseCommande(id);
        return ResponseEntity.ok().build();
    }
}
