package com.example.backofficeproject.controller;

import com.example.backofficeproject.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommendations/{clientId}")
    public Map<String, Object> getClientRecommendations(@PathVariable Long clientId) {
        return recommendationService.getRecommendationsForClient(clientId);
    }
}
