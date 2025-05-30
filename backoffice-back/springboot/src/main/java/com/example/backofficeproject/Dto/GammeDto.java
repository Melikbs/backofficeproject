package com.example.backofficeproject.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GammeDto {
    private Long codeGamme;
    private String libelle;
    private Boolean flag;
}