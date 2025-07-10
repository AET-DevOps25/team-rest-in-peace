package com.example.data_fetching_service;

import com.example.data_fetching_service.dto.BundestagApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    @Autowired
    private ApiService apiService;

    @GetMapping("/fetch")
    public void triggerDataFetch(
            @RequestParam(name = "f.aktualisiert.start", required = false) String aktualisiertStart,
            @RequestParam(name = "f.aktualisiert.end", required = false) String aktualisiertEnd,
            @RequestParam(name = "f.datum.start", required = false) String datumStart,
            @RequestParam(name = "f.datum.end", required = false) String datumEnd,
            @RequestParam(name = "f.dokumentnummer", required = false) String dokumentnummer,
            @RequestParam(name = "f.id", required = false) String id,
            @RequestParam(name = "f.vorgangstyp", required = false) String vorgangstyp,
            @RequestParam(name = "f.vorgangstyp_notation", required = false) String vorgangstypNotation,
            @RequestParam(name = "f.wahlperiode", required = false) String wahlperiode,
//            @RequestParam(name = "f.zuordnung", required = false) String zuordnung,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "format", required = false, defaultValue = "json") String format) { // Default to json as per your request

        apiService.fetchAndStoreData(
                aktualisiertStart, aktualisiertEnd,
                datumStart, datumEnd,
                dokumentnummer, id,
                vorgangstyp, vorgangstypNotation,
                wahlperiode,
                cursor, format
        );

    }
}