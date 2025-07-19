package com.rip.browsing_service;

import com.rip.browsing_service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Browsing Service", description = "INTERNAL API for browsing parliamentary protocols, speeches, and statistics. Its main purpose is to serve the client.")
public class Controller {

    @Autowired
    private BrowsingService browsingService;

    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> health() {
        Map<String, Boolean> body = Collections.singletonMap("healthy", true);
        return ResponseEntity.ok(body);
    }

    @Operation(
        summary = "Get system statistics",
        description = "Returns overall statistics about the parliamentary data system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StatisticsDto.class))
        )
    })
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDto> getStatistics() {
        StatisticsDto statistics = browsingService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Get all plenary protocols",
        description = "Returns a paginated list of all plenary protocols with sorting options"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Plenary protocols retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/plenary-protocols")
    public ResponseEntity<Page<PlenaryProtocolDto>> getAllPlenaryProtocols(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "date")
            @RequestParam(defaultValue = "date") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PlenaryProtocolDto> result = browsingService.getAllPlenaryProtocols(pageable);

        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Get speaker statistics",
        description = "Returns paginated statistics for all speakers in the parliamentary system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Speaker statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/speakers")
    public ResponseEntity<Page<SpeakerStatisticDto>> getAllSpeakerStatistics(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SpeakerStatisticDto> stats = browsingService.getAllSpeakerStatistics(pageable);
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Get party statistics",
        description = "Returns statistics for all political parties"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Party statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        )
    })
    @GetMapping("/parties")
    public ResponseEntity<List<PartyStatisticsDto>> getAllPartyStatistics() {
        List<PartyStatisticsDto> stats = browsingService.getAllPartyStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Search and filter speeches",
        description = "Returns paginated speeches with optional filtering by parties, speakers, protocols, and text search with similarity matching"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Speeches retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
        )
    })
    @GetMapping("/speeches")
    public Page<SpeechDto> getAllSpeeches(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Filter by party names", example = "[\"CDU\", \"SPD\"]")
            @RequestParam(required = false) Optional<List<String>> parties,

            @Parameter(description = "Filter by speaker IDs", example = "[1, 2, 3]")
            @RequestParam(required = false) Optional<List<Integer>> speakerIds,

            @Parameter(description = "Filter by plenary protocol ID", example = "123")
            @RequestParam(required = false) Integer plenaryProtocolId,

            @Parameter(description = "Text to search for in speeches", example = "climate change")
            @RequestParam(required = false) String searchText,

            @Parameter(description = "Similarity threshold for text search (0.0-1.0)", example = "0.5")
            @RequestParam(defaultValue = "0.5") Float searchSimilarityThreshold
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return browsingService.getAllSpeechDetails(pageable, parties.orElse(new ArrayList<>()), speakerIds.orElse(new ArrayList<>()), plenaryProtocolId, searchText, searchSimilarityThreshold);
    }

    @Operation(
        summary = "Get plenary protocol name by ID",
        description = "Returns the name/title of a specific plenary protocol"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Protocol name retrieved successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Protocol not found"
        )
    })
    @GetMapping("plenary-protocols/{id}/name")
    public ResponseEntity<String> getPlenaryProtocolName(
            @Parameter(description = "Plenary protocol ID", example = "123")
            @PathVariable int id
    ) {
        String name = browsingService.getPlenaryProtocolName(id);
        return ResponseEntity.ok(name);
    }

    @Operation(
        summary = "Get speaker name by ID",
        description = "Returns the name of a specific speaker"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Speaker name retrieved successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Speaker not found"
        )
    })
    @GetMapping("speaker/{id}/name")
    public ResponseEntity<String> getSpeakerName(
            @Parameter(description = "Speaker ID", example = "456")
            @PathVariable int id
    ) {
        String name = browsingService.getSpeakerName(id);
        return ResponseEntity.ok(name);
    }
}