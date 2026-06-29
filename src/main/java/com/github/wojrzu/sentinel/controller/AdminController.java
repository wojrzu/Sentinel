package com.github.wojrzu.sentinel.controller;

import com.github.wojrzu.sentinel.dto.RegisterRequest;
import com.github.wojrzu.sentinel.dto.UpdateUserRequest;
import com.github.wojrzu.sentinel.model.*;
import com.github.wojrzu.sentinel.repository.UserRepository;
import com.github.wojrzu.sentinel.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody RegisterRequest request, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.createUser(request, caller);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/users/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID userId, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userService.deactivateAccount(userService.getById(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID userId, @RequestBody UpdateUserRequest request, @AuthenticationPrincipal User caller) {
        if (!(caller instanceof Admin)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getById(userId);

        if (request.getAccountActive() != null) {
            if (request.getAccountActive()) {
                userService.activateAccount(user);
            } else {
                userService.deactivateAccount(user);
            }
        }
        if (user instanceof Client client) {
            if (request.getOwnedPlan() != null || request.getSubscriptionActive() != null) {
                boolean sub = request.getSubscriptionActive() != null ? request.getSubscriptionActive() : client.isSubscriptionActive();
                int plan = request.getOwnedPlan() != null ? request.getOwnedPlan() : client.getOwnedPlan();
                if (sub) {
                    userService.activateSubscription(client, plan);
                } else {
                    userService.deactivateSubscription(client);
                }
            }
        } else if ((user instanceof Officer || user instanceof Dispatcher) && request.getStatus() != null) {
            userService.updateStatus(user, request.getStatus());
        }

        return ResponseEntity.noContent().build();
    }
}