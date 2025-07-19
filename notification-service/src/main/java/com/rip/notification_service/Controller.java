package com.rip.notification_service;

import com.rip.notification_service.dto.NotificationRequest;
import com.rip.notification_service.dto.SubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Notification Service", description = "INTERNAL API - for managing user subscriptions and sending notifications about new parliamentary protocols")
public class Controller {
    @Autowired
    private ApiService apiService;

    @Operation(
        summary = "Health check endpoint",
        description = "Returns the health status of the notification service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Service is healthy and ready to process notifications",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Collections.singletonMap("healthy", true);
    }

    @Operation(
        summary = "Subscribe to notifications",
        description = "Creates a new subscription for a user to receive notifications about new parliamentary protocols. " +
                     "Users can subscribe with their email and specify notification preferences."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Subscription created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{\"success\": true}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid subscription request or email already subscribed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{\"success\": false, \"error\": \"Email already subscribed\"}")
            )
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Validation error in request body"
        )
    })
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(
            @Parameter(
                description = "Subscription details including email and notification preferences",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SubscriptionRequest.class),
                    examples = @ExampleObject(
                        name = "Basic subscription",
                        value = "{\"email\": \"user@example.com\", \"notificationTypes\": [\"NEW_PROTOCOL\"], \"active\": true}"
                    )
                )
            )
            @Valid @RequestBody SubscriptionRequest req
    ) {
        ApiService.Result result = apiService.addSubscription(req);
        Map<String, Object> body = new HashMap<>();
        body.put("success", result.success());
        if (!result.success()) {
            body.put("error", result.errorMessage());
        }
        return result.success()
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Operation(
        summary = "Unsubscribe from all notifications",
        description = "Removes all subscriptions for a given email address. This will stop all notifications for the user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully unsubscribed user",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{\"success\": true}")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Email not found or unsubscribe failed",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{\"success\": false, \"error\": \"Email not found\"}")
            )
        )
    })
    @DeleteMapping("/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribe(
            @Parameter(
                description = "Email address to unsubscribe from all notifications",
                required = true,
                example = "user@example.com"
            )
            @RequestParam("email") String email
    ) {
        ApiService.Result result = apiService.deleteAllSubscriptions(email);
        Map<String, Object> body = new HashMap<>();
        body.put("success", result.success());
        if (!result.success()) {
            body.put("error", result.errorMessage());
        }
        return result.success()
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Operation(
        summary = "Send notifications to subscribers",
        description = "Triggers notifications to all subscribers about new plenary protocols. " +
                     "This endpoint processes notifications asynchronously and returns immediately. " +
                     "Typically called by the data fetching service when new protocols are available."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "202",
            description = "Notification processing started successfully. Notifications will be sent asynchronously."
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid notification request"
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Validation error in request body"
        )
    })
    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> notifyUsers(
            @Parameter(
                description = "Notification request containing the IDs of new plenary protocols",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = NotificationRequest.class),
                    examples = @ExampleObject(
                        name = "New protocols notification",
                        value = "{\"plenaryProtocolIds\": [12345, 12346, 12347]}"
                    )
                )
            )
            @Valid @RequestBody NotificationRequest req
    ) {
        apiService.notifyAllAsync(req.getPlenaryProtocolIds());
        return ResponseEntity.accepted().build();
    }
}