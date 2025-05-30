package com.example.backofficeproject.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaracteristiqueRequest {
    private String valeur;
    private String theme;
}
