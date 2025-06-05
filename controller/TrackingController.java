package com.p2p.transport.controller;

import com.p2p.transport.model.Tracking;
// Remove this import to avoid conflict:
// import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Operation(summary = "Update driver location", description = "Driver updates their location for a ride.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<com.p2p.transport.response.ApiResponse<Tracking>> updateLocation(
            @RequestBody UpdateLocationRequest request) {
        try {
            Tracking tracking = trackingService.updateLocation(
                    request.rideId(), request.latitude(), request.longitude());
            com.p2p.transport.response.ApiResponse<Tracking> response =
                    new com.p2p.transport.response.ApiResponse<>(HttpStatus.OK.value(), "Location updated successfully", tracking);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            com.p2p.transport.response.ApiResponse<Tracking> response =
                    new com.p2p.transport.response.ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get latest location", description = "Retrieve the latest location for a ride.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Location retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No tracking data found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{rideId}")
    public ResponseEntity<com.p2p.transport.response.ApiResponse<Tracking>> getLatestLocation(@PathVariable UUID rideId) {
        try {
            Tracking tracking = trackingService.getLatestLocation(rideId);
            com.p2p.transport.response.ApiResponse<Tracking> response =
                    new com.p2p.transport.response.ApiResponse<>(HttpStatus.OK.value(), "Location retrieved successfully", tracking);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            com.p2p.transport.response.ApiResponse<Tracking> response =
                    new com.p2p.transport.response.ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    public record UpdateLocationRequest(UUID rideId, double latitude, double longitude) {}
}