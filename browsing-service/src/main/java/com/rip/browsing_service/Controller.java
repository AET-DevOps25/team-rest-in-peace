package com.rip.browsing_service;

import com.rip.browsing_service.dto.PlenaryProtocolDto;
import com.rip.browsing_service.dto.SpeakerStatisticDto;
import com.rip.browsing_service.dto.SpeechDto;
import com.rip.browsing_service.dto.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/speeches")
    public Page<SpeechDto> getAllSpeeches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String party,
            @RequestParam(required = false) Integer speakerId,
            @RequestParam(required = false) Integer plenaryProtocolId
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return browsingService.getAllSpeechDetails(pageable, party, speakerId, plenaryProtocolId);
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
