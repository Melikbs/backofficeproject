package com.example.backofficeproject.service;


import com.example.backofficeproject.Dto.DashboardStatisticsDTO;
import com.example.backofficeproject.model.*;
import com.example.backofficeproject.repositories.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CommandeRepository commandeRepository;

    public DashboardStatisticsDTO getStatistics() {
        List<Commande> commandes = commandeRepository.findAll();

        ZoneId tunisZone = ZoneId.of("Africa/Tunis");
        LocalDate today = LocalDate.now(tunisZone);
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate monthStart = today.withDayOfMonth(1);

        long salesToday = 0, salesThisWeek = 0, salesThisMonth = 0;
        double revenueToday = 0, revenueThisWeek = 0, revenueThisMonth = 0;
        long pendingOrders = 0;

        Map<String, Long> productSales = new HashMap<>();
        Map<String, Integer> clientPurchases = new HashMap<>();

        for (Commande commande : commandes) {
            if (commande.getStatus() == CommandeStatus.PENDING) {
                pendingOrders++;
                continue;
            }

            if (commande.getStatus() != CommandeStatus.VALIDATED || commande.getDateValidation() == null)
                continue;

            LocalDate validationLocalDate = commande.getDateValidation()
                    .toInstant()
                    .atZone(tunisZone)
                    .toLocalDate();

            if (validationLocalDate.isEqual(today)) salesToday++;
            if (!validationLocalDate.isBefore(weekStart)) salesThisWeek++;
            if (!validationLocalDate.isBefore(monthStart)) salesThisMonth++;

            List<LigneCommande> lignes = commande.getLignes();
            if (lignes == null || lignes.isEmpty()) continue;

            double totalCommande = lignes.stream()
                    .filter(l -> l.getProduit() != null && l.getProduit().getPrix() != null)
                    .mapToDouble(l -> l.getProduit().getPrix() * l.getQuantite())
                    .sum();

            if (totalCommande > 0) {
                if (validationLocalDate.isEqual(today)) revenueToday += totalCommande;
                if (!validationLocalDate.isBefore(weekStart)) revenueThisWeek += totalCommande;
                if (!validationLocalDate.isBefore(monthStart)) revenueThisMonth += totalCommande;
            }

            Client c = commande.getClient();
            String clientName = (c.getPrenom() != null ? c.getPrenom() + " " : "") +
                    (c.getNom() != null ? c.getNom() : "Inconnu");

            int totalQty = lignes.stream().mapToInt(LigneCommande::getQuantite).sum();
            clientPurchases.merge(clientName, totalQty, Integer::sum);

            for (LigneCommande ligne : lignes) {
                if (ligne.getProduit() == null || ligne.getProduit().getLibelle() == null) continue;
                productSales.merge(
                        ligne.getProduit().getLibelle(),
                        (long) ligne.getQuantite(),
                        Long::sum
                );
            }
        }

        Map<String, Long> topProducts = productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String, Integer> topClients = clientPurchases.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        DashboardStatisticsDTO stats = new DashboardStatisticsDTO();
        stats.setSalesToday(salesToday);
        stats.setSalesThisWeek(salesThisWeek);
        stats.setSalesThisMonth(salesThisMonth);
        stats.setRevenueToday(revenueToday);
        stats.setRevenueThisWeek(revenueThisWeek);
        stats.setRevenueThisMonth(revenueThisMonth);
        stats.setTopProducts(topProducts);
        stats.setPendingOrders(pendingOrders);
        stats.setTopClients(topClients);


        stats.setAiSummary("No AI summary yet.");


        return stats;
    }
}
