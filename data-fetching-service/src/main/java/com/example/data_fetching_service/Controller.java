package com.example.data_fetching_service;

import com.example.data_fetching_service.dto.BundestagApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.List;

@RestController
@Tag(name = "Data Fetching Service", description = "INTERNAL API - for fetching and storing parliamentary data from the Bundestag API")
public class Controller {
    @Autowired
    private ApiService apiService;

    @Operation(
        summary = "Fetch parliamentary data from Bundestag API",
        description = "Fetches parliamentary protocol data from the official Bundestag API with extensive filtering options. " +
                     "Supports date ranges, document types, election periods, and more. Optionally notifies subscribers of new data."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Data fetching completed successfully. Returns list of successfully processed plenary protocol IDs.",
            content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Integer.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request parameters or date format"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error occurred while fetching or storing data"
        )
    })
    @GetMapping("/fetch")
    public ResponseEntity<?> triggerDataFetch(
            @Parameter(
                description = "Whether to notify subscribers about newly fetched data",
                example = "true"
            )
            @RequestParam(name = "notify", defaultValue = "false", required = false) boolean notifySubscribers,

            @Parameter(
                description = "Filter by update start date (ISO 8601 format: YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)",
                example = "2024-01-01"
            )
            @RequestParam(name = "f.aktualisiert.start", required = false) String aktualisiertStart,

            @Parameter(
                description = "Filter by update end date (ISO 8601 format: YYYY-MM-DD or YYYY-MM-DDTHH:MM:SS)",
                example = "2024-12-31"
            )
            @RequestParam(name = "f.aktualisiert.end", required = false) String aktualisiertEnd,

            @Parameter(
                description = "Filter by document start date (ISO 8601 format: YYYY-MM-DD)",
                example = "2024-01-01"
            )
            @RequestParam(name = "f.datum.start", required = false) String datumStart,

            @Parameter(
                description = "Filter by document end date (ISO 8601 format: YYYY-MM-DD)",
                example = "2024-12-31"
            )
            @RequestParam(name = "f.datum.end", required = false) String datumEnd,

            @Parameter(
                description = "Filter by specific document number",
                example = "20/1"
            )
            @RequestParam(name = "f.dokumentnummer", required = false) String dokumentnummer,

            @Parameter(
                description = "Filter by specific document ID",
                example = "12345"
            )
            @RequestParam(name = "f.id", required = false) String id,

            @Parameter(
                description = "Filter by process type (e.g., 'Plenarprotokoll')",
                example = "Plenarprotokoll"
            )
            @RequestParam(name = "f.vorgangstyp", required = false) String vorgangstyp,

            @Parameter(
                description = "Filter by process type notation/code",
                example = "pp"
            )
            @RequestParam(name = "f.vorgangstyp_notation", required = false) String vorgangstypNotation,

            @Parameter(
                description = "Filter by election period number",
                example = "20"
            )
            @RequestParam(name = "f.wahlperiode", required = false) String wahlperiode,

            @Parameter(
                description = "Pagination cursor for fetching next set of results",
                example = "eyJpZCI6MTIzNDU2fQ=="
            )
            @RequestParam(name = "cursor", required = false) String cursor,

            @Parameter(
                description = "Response format from Bundestag API",
                example = "json",
                schema = @Schema(allowableValues = {"json", "xml"})
            )
            @RequestParam(name = "format", defaultValue = "json") String format
    ) {
        ApiService.Result result = apiService.fetchAndStoreData(aktualisiertStart, aktualisiertEnd, datumStart, datumEnd, dokumentnummer, id, vorgangstyp, vorgangstypNotation, wahlperiode, cursor, format);

        List<Integer> ids = result.successfulPlenaryProtocolIds();
        if (notifySubscribers && !ids.isEmpty()) {
            apiService.callNotificationService(ids);
        }

        return ResponseEntity.ok(ids);
    }

    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the data fetching service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy and ready to fetch data",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Collections.singletonMap("healthy", true);
    }
}