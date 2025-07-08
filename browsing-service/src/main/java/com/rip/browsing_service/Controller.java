package com.rip.browsing_service;
import com.rip.browsing_service.dto.PlenaryProtocolDto;
import com.rip.browsing_service.dto.StatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}
