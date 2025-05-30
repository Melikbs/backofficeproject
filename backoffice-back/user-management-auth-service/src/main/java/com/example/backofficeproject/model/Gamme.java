package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Gamme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeGamme;

    private String libelle;

    private boolean flag;
    @OneToMany(mappedBy = "gamme")
    private List<Marque> marques;
}