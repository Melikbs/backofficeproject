package com.example.backofficeproject.Dto;

import lombok.Data;

@Data
public class PackProduitDto {
    private Long codeProduit;
    private Double reductionPourcentage;
    private Double reductionValeur;
}