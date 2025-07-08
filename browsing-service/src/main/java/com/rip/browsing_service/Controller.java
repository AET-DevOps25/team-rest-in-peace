package com.rip.browsing_service;
import com.rip.browsing_service.dto.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private BrowsingService browsingService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto statistics = browsingService.getStatistics();
        return ResponseEntity.ok(statistics);
    }
}
