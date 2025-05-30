package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeStock;
    private Integer nombreStock;
    private Boolean inStock;
    @ManyToOne
    @JoinColumn(name = "code_produit")
    private Produit produit;
    @ManyToOne
    @JoinColumn(name = "code_couleur")
    private Couleur couleur;


}