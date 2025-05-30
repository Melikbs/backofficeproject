package com.example.backofficeproject.controller;

import com.example.backofficeproject.Dto.ChatMessageDTO;
import com.example.backofficeproject.Dto.ChatResponseDTO;
import com.example.backofficeproject.Dto.DashboardStatisticsDTO;
import com.example.backofficeproject.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final DashboardService dashboardService;

    public ChatbotController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("response", "❌ Message is required"));
        }

        Map<String, Object> request = Map.of("message", message);
        String fastApiUrl = "http://localhost:11434/api/chat";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, request, Map.class);
            String reply = (String) response.getBody().get("response");
            return ResponseEntity.ok(Map.of("response", reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("response", "⚠️ AI Assistant not available."));
        }
    }

}
