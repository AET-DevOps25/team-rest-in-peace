package com.example.data_fetching_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final ApiService apiService;

    public ScheduledTasks(ApiService apiService) {
        this.apiService = apiService;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Europe/Berlin")
    public void dailyDataFetch() {
        logger.info("Starting daily data fetch");
        LocalDateTime midnight = LocalDate
                .now(ZoneId.of("Europe/Berlin"))
                .atStartOfDay();
        String today = midnight
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        today = "2025-07-09T00:00:00";
        apiService.fetchAndStoreData(
                today,  // aktualisiertStart
                null,  // aktualisiertEnd
                null, // datumStart,
                null,  // dokumentnummer
                null,  // id
                null,  // vorgangstyp
                null,  // vorgangstypNotation
                null,  // wahlperiode
                null,  // zuordnung
                null,  // cursor
                "json" // format
        );
    }
}