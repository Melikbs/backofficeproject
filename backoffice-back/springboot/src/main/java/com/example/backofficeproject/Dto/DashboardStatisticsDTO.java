package com.example.backofficeproject.Dto;

import lombok.*;

import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatisticsDTO {
    private long salesToday;
    private long salesThisWeek;
    private long salesThisMonth;

    private double revenueToday;
    private double revenueThisWeek;
    private double revenueThisMonth;

    private Map<String, Long> topProducts;
    private long pendingOrders;
    private Map<String, Integer> topClients;

    private String aiSummary; // Make sure this is added for GPT support
}
