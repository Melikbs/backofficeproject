package com.example.backofficeproject.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @MessageMapping("/notify") // --> /app/notify
    @SendTo("/topic/notifications") // broadcast to everyone listening
    public String sendNotification(String message) {
        return message;
    }
}
