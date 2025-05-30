package com.example.backofficeproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commandes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codeCommande;

    @Temporal(TemporalType.DATE)
    private Date dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandeStatus status = CommandeStatus.PENDING;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateValidation;


    @ManyToOne
    @JoinColumn(name = "code_client")
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<LigneCommande> lignes;
}
