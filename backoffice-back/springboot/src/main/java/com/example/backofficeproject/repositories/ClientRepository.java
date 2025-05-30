package com.example.backofficeproject.repositories;

import com.example.backofficeproject.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByActifTrue();

    List<Client> findAllByActifFalse();
    @Query("""
    SELECT DISTINCT c
    FROM Client c
    JOIN Commande cmd ON cmd.client.codeClient = c.codeClient
    JOIN LigneCommande lc ON lc.commande.codeCommande = cmd.codeCommande
""")
    List<Client> findAllAcheteurs();

}