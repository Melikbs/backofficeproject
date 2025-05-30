package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.GammeDto;
import com.example.backofficeproject.mapper.GammeMapper;
import com.example.backofficeproject.model.Gamme;
import com.example.backofficeproject.repositories.GammeRepository;
import com.example.backofficeproject.service.GammeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/gammes")
public class GammeController {
    @Autowired
    private GammeService gammeService;
@Autowired
private GammeRepository gammeRepository;
    @GetMapping
    public List<GammeDto> all() {
        return gammeService.getAll();
    }

    @GetMapping("/{code_gamme}")
    public GammeDto one(@PathVariable("code_gamme") Long codeGamme) {
        return gammeService.getById(codeGamme);
    }

    @PostMapping("/create")
    public GammeDto create(@RequestBody GammeDto dto) {
        return gammeService.add(dto);
    }

    @PutMapping("/flag/{code_gamme}")
    public GammeDto toggleFlag(@PathVariable("code_gamme") Long codeGamme) {
        return gammeService.toggleFlag(codeGamme);
    }

    @PutMapping("/update/{codeGamme}")
    public ResponseEntity<Gamme> updateGamme(@PathVariable Long codeGamme, @RequestBody Gamme updatedGamme) {
        Optional<Gamme> existingGamme = gammeRepository.findById(codeGamme);
        if (existingGamme.isPresent()) {
            Gamme gamme = existingGamme.get();
            gamme.setLibelle(updatedGamme.getLibelle());
            gamme.setFlag(updatedGamme.isFlag());
            return ResponseEntity.ok(gammeRepository.save(gamme));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{code_gamme}")
    public ResponseEntity<Void> deleteGamme(@PathVariable("code_gamme") Long codeGamme) {
        gammeService.deleteGamme(codeGamme);
        return ResponseEntity.noContent().build();
    }
}
