package com.example.backofficeproject.controller;


import com.example.backofficeproject.Dto.AIComparisonResult;
import com.example.backofficeproject.service.AIAgentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // You can restrict this if needed
public class AIAgentController {

    private final AIAgentService aiAgentService;

    public AIAgentController(AIAgentService aiAgentService) {
        this.aiAgentService = aiAgentService;
    }

    @GetMapping("/compare/{productId}")
    public AIComparisonResult compareProduct(@PathVariable int productId) {
        return aiAgentService.compareProduct(productId);
    }
}