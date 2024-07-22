package com.pch777.blog.subscription.controller;

import com.pch777.blog.subscription.domain.model.Subscription;
import com.pch777.blog.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/subscriptions")
public class SubscriptionApiController {

    private final SubscriptionService subscriptionService;

    @GetMapping("{id}")
    public Subscription getSubscriptionById(@PathVariable UUID id) {
        return subscriptionService.getSubscriptionById(id);
    }

    @GetMapping("authors/{id}")
    public List<Subscription> getSubscriptionsByAuthorId(@PathVariable UUID id) {
        return subscriptionService.getSubscriptionsByAuthorId(id);
    }

    @GetMapping("readers/{id}")
    public List<Subscription> getSubscriptionsByReaderId(@PathVariable UUID id) {
        return subscriptionService.getSubscriptionsByReaderId(id);
    }


    @PostMapping("/authors/{id}")
    public Subscription subscribeToAuthor(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return subscriptionService.subscribeToAuthor(id, userDetails.getUsername());
    }


    @GetMapping("/authors/{id}/check")
    public ResponseEntity<Map<String, Boolean>> checkSubscriptionForAllUsers(@PathVariable UUID id,
                                                                             @AuthenticationPrincipal UserDetails userDetails) {
        boolean hasSubscription = false;
        if (userDetails != null) {
            hasSubscription = subscriptionService.hasSubscription(id, userDetails.getUsername());
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasSubscription", hasSubscription);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/authors/{id}")
    public void deleteSubscription(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
            subscriptionService.unsubscribeFromAuthor(id, userDetails.getUsername());
    }


}
