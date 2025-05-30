package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.ProduitRequestDto;
import com.example.backofficeproject.Dto.ProduitResponseDto;
import com.example.backofficeproject.mapper.ProduitMapper;
import com.example.backofficeproject.model.Produit;
import com.example.backofficeproject.service.FileStorageService;
import com.example.backofficeproject.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final ProduitMapper produitMapper;
private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<ProduitResponseDto> createProduit(@RequestParam("file") MultipartFile file,
                                                            @RequestParam("libelle") String libelle,
                                                            @RequestParam("description") String description,
                                                            @RequestParam("prix") double prix,
                                                            @RequestParam("poids") double poids,
                                                            @RequestParam("actif") boolean actif,
                                                            @RequestParam("codeGamme") Long codeGamme,
                                                            @RequestParam("codeMarque") Long codeMarque) throws IOException {
        String imageName = fileStorageService.saveImage(file);
        System.out.println("codeGamme: " + codeGamme + " / codeMarque: " + codeMarque);

        ProduitRequestDto produitRequestDto = ProduitRequestDto.builder()
                .libelle(libelle)
                .description(description)
                .prix(prix)
                .poids(poids)
                .actif(actif)
                .codeGamme(codeGamme)
                .codeMarque(codeMarque)
                .image(imageName)
                .build();

        Produit produit = produitService.createProduit(produitRequestDto);
        ProduitResponseDto responseDto = produitMapper.toResponseDto(produit);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource resource = fileStorageService.loadImageAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProduitResponseDto> updateProduit(@PathVariable Long id,
                                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                                            @RequestParam("libelle") String libelle,
                                                            @RequestParam("description") String description,
                                                            @RequestParam("prix") double prix,
                                                            @RequestParam("poids") double poids,
                                                            @RequestParam("actif") boolean actif) {
        try {
            String imageName = (file != null && !file.isEmpty()) ? fileStorageService.saveImage(file) : null;

            ProduitRequestDto produitRequestDto = ProduitRequestDto.builder()
                    .libelle(libelle)
                    .description(description)
                    .prix(prix)
                    .poids(poids)
                    .actif(actif)
                    .image(imageName)
                    .build();

            Produit produit = produitService.updateProduit(id, produitRequestDto);
            ProduitResponseDto responseDto = produitMapper.toResponseDto(produit);
            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            e.printStackTrace(); // ou log.error(...)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    public ResponseEntity<List<ProduitResponseDto>> getAllProduits() {
        List<Produit> produits = produitService.getAllProduits();
        List<ProduitResponseDto> responseDtos = produits.stream()
                .map(produitMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponseDto> getProduitById(@PathVariable Long id) {
        Produit produit = produitService.getProduitById(id);
        ProduitResponseDto responseDto = produitMapper.toResponseDto(produit);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-activation")
    public ResponseEntity<Void> toggleActivation(@PathVariable Long id) {
        produitService.toggleActivation(id);
        return ResponseEntity.noContent().build();
    }
}
