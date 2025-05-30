package com.example.backofficeproject.mapper;

import com.example.backofficeproject.Dto.ClientDto;
import com.example.backofficeproject.Dto.CommandeDetailDTO;
import com.example.backofficeproject.Dto.CommandeResponseDTO;
import com.example.backofficeproject.Dto.LigneCommandeDTO;
import com.example.backofficeproject.model.Client;
import com.example.backofficeproject.model.Commande;
import com.example.backofficeproject.model.LigneCommande;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandeMapper {

    public CommandeResponseDTO toCommandeResponse(Commande commande) {
        return new CommandeResponseDTO(
                commande.getCodeCommande(),
                commande.getDateCommande(),
                commande.getStatus(),
                commande.getClient().getNom(),
                commande.getClient().getPrenom()
        );
    }

    public ClientDto toClientDTO(Client client) {
        return new ClientDto(
                client.getCodeClient(),     // ✅ ID
                client.getUsername(),       // ✅ Username
                client.getNom(),            // ✅ Nom
                client.getPrenom(),         // ✅ Prénom
                client.getCin(),            // ✅ CIN
                client.getTel(),            // ✅ Téléphone
                client.getEmail(),          // ✅ Email
                client.getRue(),            // ✅ Rue
                client.getVille(),          // ✅ Ville
                client.getCodePostal(),     // ✅ Code postal
                client.isActif()            // ✅ Actif
        );
    }


    public LigneCommandeDTO toLigneDTO(LigneCommande ligne) {
        LigneCommandeDTO dto = new LigneCommandeDTO();
        dto.setLibelleProduit(ligne.getProduit().getLibelle());
        dto.setPrix(ligne.getProduit().getPrix());
        dto.setQuantite(ligne.getQuantite());
        dto.setPoids(ligne.getProduit().getPoids());  // 🔥 Map the weight
        return dto;
    }


    public CommandeDetailDTO toDetailDTO(Commande commande, List<LigneCommandeDTO> lignes) {
        double sousTotal = lignes.stream().mapToDouble(l -> l.getPrix() * l.getQuantite()).sum();
        double tva = sousTotal * 0.17;
        double total = sousTotal + tva;

        return new CommandeDetailDTO(
                commande.getCodeCommande(),
                commande.getDateCommande(),
                toClientDTO(commande.getClient()),lignes,
                "Carte bancaire", // static for now
                sousTotal,
                tva,
                total,
                commande.getStatus()
        );
    }
}
