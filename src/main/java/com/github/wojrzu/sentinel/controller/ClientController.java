package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.model.Client;
import com.github.wojrzu.sentinel.model.User;
import com.github.wojrzu.sentinel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
@AllArgsConstructor
public class ClientController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(Map.of(
                "subscriptionActive", client.isSubscriptionActive(),
                "ownedPlan",          client.getOwnedPlan(),
                "firstName",          client.getFirstName() != null ? client.getFirstName() : "",
                "lastName",           client.getLastName()  != null ? client.getLastName()  : "",
                "email",              client.getEmail()     != null ? client.getEmail()     : "",
                "username",           client.getUsername()
        ));
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(@RequestBody Map<String, String> body, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Client client)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (body.containsKey("firstName")) {
            client.setFirstName(body.get("firstName"));
        }

        if (body.containsKey("lastName")){
            client.setLastName(body.get("lastName"));
        }

        if (body.containsKey("email")){
            client.setEmail(body.get("email"));
        }

        userService.getClientById(client.getUserId());
        return ResponseEntity.noContent().build();
    }
}