package com.rip.notification_service;

import com.rip.notification_service.dto.NotificationRequest;
import com.rip.notification_service.dto.SubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {
    @Autowired
    private ApiService apiService;

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Collections.singletonMap("healthy", true);
    }


    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(@Valid @RequestBody SubscriptionRequest req) {
        ApiService.Result result = apiService.addSubscription(req);
        Map<String, Object> body = new HashMap<>();
        body.put("success", result.success());
        if (!result.success()) {
            body.put("error", result.errorMessage());
        }
        return result.success()
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribe(@RequestParam("email") String email) {
        ApiService.Result result = apiService.deleteAllSubscriptions(email);
        Map<String, Object> body = new HashMap<>();
        body.put("success", result.success());
        if (!result.success()) {
            body.put("error", result.errorMessage());
        }
        return result.success()
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }


    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> notifyUsers(@Valid @RequestBody NotificationRequest req) {
        apiService.notifyAllAsync(req.getPlenaryProtocolIds());
        return ResponseEntity.accepted().build();
    }
}

