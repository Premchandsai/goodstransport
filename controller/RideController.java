package com.p2p.transport.controller;

import com.p2p.transport.model.Ride;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @Operation(summary = "Create a new ride", description = "Allows a Driver or Admin to create a new ride.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ride created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Ride>> createRide(@RequestBody Ride ride) {
        Ride createdRide = rideService.createRide(ride);
        ApiResponse<Ride> response = new ApiResponse<>(HttpStatus.OK.value(), "Ride created successfully", createdRide);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Search for rides", description = "Search available rides by departure, destination, and time.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rides retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Ride>>> searchRides(@RequestParam String departure, @RequestParam String destination, @RequestParam LocalDateTime time) {
        List<Ride> rides = rideService.searchRides(departure, destination, time);
        ApiResponse<List<Ride>> response = new ApiResponse<>(HttpStatus.OK.value(), "Rides retrieved successfully", rides);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Update a ride", description = "Allows a Driver or Admin to update an existing ride.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ride updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Ride>> updateRide(@PathVariable UUID id, @RequestBody Ride ride) {
        Ride updatedRide = rideService.updateRide(id, ride);
        ApiResponse<Ride> response = new ApiResponse<>(HttpStatus.OK.value(), "Ride updated successfully", updatedRide);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cancel a ride", description = "Allows a Driver or Admin to cancel a ride.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Ride cancelled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelRide(@PathVariable UUID id) {
        rideService.cancelRide(id);
        ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Ride cancelled successfully", null);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}