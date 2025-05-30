package com.example.backofficeproject.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiApiUrl = "http://localhost:8083/recommendations/";

    public Map<String, Object> getRecommendationsForClient(Long clientId) {
        String url = aiApiUrl + clientId;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }
}