package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.DashboardStatisticsDTO;
import com.example.backofficeproject.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            DashboardStatisticsDTO stats = dashboardService.getStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Ajoute ça pour voir le vrai détail de l'erreur
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }
}
