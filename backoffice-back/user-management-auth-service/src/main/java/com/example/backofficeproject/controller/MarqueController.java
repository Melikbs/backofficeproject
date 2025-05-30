package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.MarqueDto;
import com.example.backofficeproject.service.MarqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marques")
@RequiredArgsConstructor
public class MarqueController {

    private final MarqueService marqueService;

    private final SimpMessagingTemplate messagingTemplate;
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MARKETING')")
    public List<MarqueDto> getAll() {
        return marqueService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MARKETING')")
    public MarqueDto get(@PathVariable Long id) {
        return marqueService.getById(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_MARKETING')")
    public MarqueDto create(@RequestBody MarqueDto dto) {
        MarqueDto created = marqueService.create(dto);
        messagingTemplate.convertAndSend("/topic/notifications", "Nouvelle marque ajoutée : " + created.getLibelle());
        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MARKETING')")
    public MarqueDto update(@PathVariable Long id, @RequestBody MarqueDto dto) {
        return marqueService.update(id, dto);
    }

    @PatchMapping("/{codeMarque}/toggle-flag")
    public ResponseEntity<Void> toggleFlag(@PathVariable Long codeMarque) {
        marqueService.toggleFlag(codeMarque);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MARKETING')")
    public void delete(@PathVariable Long id) {
        marqueService.delete(id);
    }
}
