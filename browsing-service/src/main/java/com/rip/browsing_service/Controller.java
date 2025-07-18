package com.rip.browsing_service;

import com.rip.browsing_service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Array;
import java.util.*;

@RestController
public class Controller {

    @Autowired
    private BrowsingService browsingService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> health() {
        Map<String, Boolean> body = Collections.singletonMap("healthy", true);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto statistics = browsingService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/plenary-protocols")
    public ResponseEntity<Page<PlenaryProtocolDto>> getAllPlenaryProtocols(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PlenaryProtocolDto> result = browsingService.getAllPlenaryProtocols(pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/speakers")
    public ResponseEntity<Page<SpeakerStatisticDto>> getAllSpeakerStatistics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SpeakerStatisticDto> stats = browsingService.getAllSpeakerStatistics(pageable);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/parties")
    public ResponseEntity<List<PartyStatisticsDto>> getAllPartyStatistics() {
        List<PartyStatisticsDto> stats = browsingService.getAllPartyStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/speeches")
    public Page<SpeechDto> getAllSpeeches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Optional<List<String>> parties,
            @RequestParam(required = false) Optional<List<Integer>> speakerIds,
            @RequestParam(required = false) Integer plenaryProtocolId,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "0.5") Float searchSimilarityThreshold
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return browsingService.getAllSpeechDetails(pageable, parties.orElse(new ArrayList<>()), speakerIds.orElse(new ArrayList<>()), plenaryProtocolId, searchText, searchSimilarityThreshold);
    }

    @GetMapping("plenary-protocols/{id}/name")
    public ResponseEntity<String> getPlenaryProtocolName(@PathVariable int id) {
        String name = browsingService.getPlenaryProtocolName(id);
        return ResponseEntity.ok(name);
    }

    @GetMapping("speaker/{id}/name")
    public ResponseEntity<String> getSpeakerName(@PathVariable int id) {
        String name = browsingService.getSpeakerName(id);
        return ResponseEntity.ok(name);
    }
}
