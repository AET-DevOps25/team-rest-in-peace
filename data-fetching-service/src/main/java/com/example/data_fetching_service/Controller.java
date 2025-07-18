package com.example.data_fetching_service;

import com.example.data_fetching_service.dto.BundestagApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.List;

@RestController
public class Controller {
    @Autowired
    private ApiService apiService;

    @GetMapping("/fetch")
    public ResponseEntity<?> triggerDataFetch(@RequestParam(name = "notify", defaultValue = "false", required = false) boolean notifySubscribers, @RequestParam(name = "f.aktualisiert.start", required = false) String aktualisiertStart, @RequestParam(name = "f.aktualisiert.end", required = false) String aktualisiertEnd, @RequestParam(name = "f.datum.start", required = false) String datumStart, @RequestParam(name = "f.datum.end", required = false) String datumEnd, @RequestParam(name = "f.dokumentnummer", required = false) String dokumentnummer, @RequestParam(name = "f.id", required = false) String id, @RequestParam(name = "f.vorgangstyp", required = false) String vorgangstyp, @RequestParam(name = "f.vorgangstyp_notation", required = false) String vorgangstypNotation, @RequestParam(name = "f.wahlperiode", required = false) String wahlperiode, @RequestParam(name = "cursor", required = false) String cursor, @RequestParam(name = "format", defaultValue = "json") String format) {
        ApiService.Result result = apiService.fetchAndStoreData(aktualisiertStart, aktualisiertEnd, datumStart, datumEnd, dokumentnummer, id, vorgangstyp, vorgangstypNotation, wahlperiode, cursor, format);

        List<Integer> ids = result.successfulPlenaryProtocolIds();
        if (notifySubscribers) {
            apiService.callNotificationService(ids);
        }

        return ResponseEntity.ok(ids);

    }

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Collections.singletonMap("healthy", true);
    }
}
