package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeLivraison;

    private String numTracking;
    private Date dateLivraison;
    private String shippingLabel;

    @Enumerated(EnumType.STRING)
    private StatusLivraison statusLivraison;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_commande") // make foreign key explicit
    private Commande commande;
}
