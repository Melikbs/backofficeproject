package com.example.backofficeproject.service;

import com.example.backofficeproject.Dto.AIComparisonResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AIAgentService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_URL = "http://localhost:8103/api/compare/";


    public AIComparisonResult compareProduct(int productId) {
        return restTemplate.getForObject(FASTAPI_URL + productId, AIComparisonResult.class);
    }
}