package com.example.touristservice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TouristController {

    @Value("${server.port:8081}")
    private String serverPort;

    @GetMapping("/tourists")
    public Map<String, Object> getTourists() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "TOURIST-SERVICE");
        response.put("instancePort", serverPort);
        response.put("message", "Tourist service response");
        response.put("tourists", List.of(
                Map.of("id", 1, "name", "Taj Mahal", "location", "Agra"),
                Map.of("id", 2, "name", "Charminar", "location", "Hyderabad"),
                Map.of("id", 3, "name", "Konark Sun Temple", "location", "Odisha")
        ));
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "TOURIST-SERVICE", "instancePort", serverPort);
    }
}
