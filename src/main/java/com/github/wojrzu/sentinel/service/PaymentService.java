package com.github.wojrzu.sentinel.service;

import com.github.wojrzu.sentinel.model.Client;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserService userService;

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.price.bronze}")
    private String bronzePriceId;

    @Value("${stripe.price.silver}")
    private String silverPriceId;

    @Value("${stripe.price.gold}")
    private String goldPriceId;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String createCheckoutSession(Client client, int planId) throws StripeException {
        String priceId = getPriceId(planId);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/dashboard")
                .setCancelUrl(frontendUrl + "/dashboard")
                .setCustomerEmail(client.getEmail())
                .putMetadata("userId",  client.getUserId().toString())
                .putMetadata("planId",  String.valueOf(planId))
                .addLineItem(SessionCreateParams.LineItem.builder()
                            .setPrice(priceId)
                            .setQuantity(1L)
                            .build()
                ).build();

        Session session = Session.create(params);
        return session.getUrl();
    }

    public void handleWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid webhook");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("Failed checkout session"));

            String userId = session.getMetadata().get("userId");
            int planId    = Integer.parseInt(session.getMetadata().get("planId"));

            userService.getById(java.util.UUID.fromString(userId));
            Client client = userService.getClientById(java.util.UUID.fromString(userId));
            userService.activateSubscription(client, planId);
        }
    }

    private String getPriceId(int planId) {
        return switch (planId) {
            case 1 -> bronzePriceId;
            case 2 -> silverPriceId;
            case 3 -> goldPriceId;
            default -> throw new RuntimeException("Unknown plan: " + planId);
        };
    }
}