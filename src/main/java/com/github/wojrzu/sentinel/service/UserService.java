package com.github.wojrzu.sentinel.service;

import com.github.wojrzu.sentinel.dto.RegisterRequest;
import com.github.wojrzu.sentinel.model.*;
import com.github.wojrzu.sentinel.dto.UserType;
import com.github.wojrzu.sentinel.repository.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Getter
public class UserService {
    private final ClientRepository clientRepository;
    private final OfficerRepository officerRepository;
    private final DispatcherRepository dispatcherRepository;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User createUser(RegisterRequest request, User caller) {
        boolean adminCalled = caller instanceof Admin;
        if (request.getUserType() != UserType.CLIENT && !adminCalled) {
            throw new IllegalArgumentException("Only Admin can create this type account");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User with such username already exists");
        }

        User user = switch (request.getUserType()) {
            case CLIENT -> {
                Client c = new Client();
                c.setOwnedPlan(0);
                c.setSubscriptionActive(false);
                c.setEmail(request.getEmail());
                c.setFirstName(request.getFirstName());
                c.setLastName(request.getLastName());
                yield c;
            }
            case OFFICER -> {
                Officer o = new Officer();
                o.setStatus(0);
                yield o;
            }
            case DISPATCHER -> {
                Dispatcher d = new Dispatcher();
                d.setStatus(0);
                yield d;
            }
            case ADMIN -> new Admin();
        };

        user.setUserType(request.getUserType());
        user.setUsername(request.getUsername());
        user.setHashedPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setAccountActive(true);

        return userRepository.save(user);
    }

    @Transactional
    public void deactivateAccount(User user){
        user.setAccountActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activateAccount(User user){
        user.setAccountActive(true);
        userRepository.save(user);
    }

    @Transactional
    public void activateSubscription(Client client, int planId) {
        client.setOwnedPlan(planId);
        client.setSubscriptionActive(true);
        clientRepository.save(client);
    }

    @Transactional
    public void deactivateSubscription(Client client) {
        client.setOwnedPlan(0);
        client.setSubscriptionActive(false);
        clientRepository.save(client);
    }

    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public Client getClientById(UUID id) {
        return clientRepository.findById(id).orElseThrow(() -> new RuntimeException("Client not found: " + id));
    }

    @Transactional
    public void updateStatus(User user, int status) {
        if (user instanceof Officer o) {
            o.setStatus(status);
        } else if (user instanceof Dispatcher d) {
            d.setStatus(status);
        } else {
            throw new IllegalArgumentException("User type has no status field");
        }

        userRepository.save(user);
    }
}