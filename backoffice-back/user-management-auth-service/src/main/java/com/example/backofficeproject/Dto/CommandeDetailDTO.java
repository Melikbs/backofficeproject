package com.example.backofficeproject.Dto;

import com.example.backofficeproject.model.CommandeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandeDetailDTO {
    private Long codeCommande;
    private Date dateCommande;
    private ClientDto client;
    private List<LigneCommandeDTO> lignes;
    private String modePaiement;
    private double sousTotal;
    private double tva;
    private double total;
    private CommandeStatus status;

}
