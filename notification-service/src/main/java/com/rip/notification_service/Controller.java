package com.rip.notification_service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class Controller {

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Collections.singletonMap("healthy", true);
    }
}

