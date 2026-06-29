package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.model.Client;
import com.github.wojrzu.sentinel.model.User;
import com.github.wojrzu.sentinel.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-session")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody Map<String, Integer> body, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Integer planId = body.get("planId");
        if (planId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String url = paymentService.createCheckoutSession(client, planId);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            paymentService.handleWebhook(payload, sigHeader);
            return ResponseEntity.ok("ok");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}