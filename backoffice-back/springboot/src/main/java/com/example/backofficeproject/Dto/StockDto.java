package com.example.backofficeproject.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDto {
    private Long codeStock;
    private Integer nombreStock;
    private Boolean inStock;
    private Long codeProduit;
    private Long codeCouleur;
    private String libelleCouleur;


}