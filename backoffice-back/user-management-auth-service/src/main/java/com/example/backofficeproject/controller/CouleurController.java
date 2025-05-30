package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.CouleurDto;
import com.example.backofficeproject.service.CouleurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couleurs")
@RequiredArgsConstructor
public class CouleurController {

    private final CouleurService couleurService;

    @GetMapping
    public List<CouleurDto> getAll() {
        return couleurService.getAll();
    }

    @GetMapping("/{id}")
    public CouleurDto get(@PathVariable Long id) {
        return couleurService.getById(id);
    }

    @PostMapping
    public CouleurDto create(@RequestBody CouleurDto dto) {
        return couleurService.create(dto);
    }

    @PutMapping("/{id}")
    public CouleurDto update(@PathVariable Long id, @RequestBody CouleurDto dto) {
        return couleurService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        couleurService.delete(id);
        return ResponseEntity.ok("Couleur supprimée avec succès");
    }
}
