package com.example.data_fetching_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @Autowired
    private ApiService apiService;

    @GetMapping("/fetch")
    public ResponseEntity<Object> triggerDataFetch() {
        apiService.fetchAndStoreData();
        return ResponseEntity.ok().build();
    }
}