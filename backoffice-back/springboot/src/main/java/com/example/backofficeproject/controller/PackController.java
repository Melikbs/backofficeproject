package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.PackDto;
import com.example.backofficeproject.service.PackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MARKETING')")
public class PackController {

    private final PackService packService;

    @GetMapping
    public List<PackDto> getAll() {
        return packService.getAll();
    }

    @PostMapping
    public PackDto create(@RequestBody PackDto dto) {
        return packService.create(dto);
    }

    @PutMapping("/{id}")
    public PackDto update(@PathVariable Long id, @RequestBody PackDto dto) {
        return packService.update(id, dto);
    }

    @PatchMapping("/{id}/disable")
    public void disable(@PathVariable Long id) {
        packService.disable(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        packService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
