package com.p2p.transport.controller;

import com.p2p.transport.model.Driver;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @Operation(summary = "Create a driver profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Driver created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Driver>> createDriver(@RequestBody Driver driver) {
        ApiResponse<Driver> response = driverService.createDriver(driver);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Get driver by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Driver retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable UUID driverId) {
        ApiResponse<Driver> response = driverService.getDriverById(driverId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Get all drivers", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Drivers retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        ApiResponse<List<Driver>> response = driverService.getAllDrivers();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Update driver profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Driver updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PutMapping("/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> updateDriver(@PathVariable UUID driverId, @RequestBody Driver driver) {
        ApiResponse<Driver> response = driverService.updateDriver(driverId, driver);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Delete driver profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Driver deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @DeleteMapping("/{driverId}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable UUID driverId) {
        ApiResponse<Void> response = driverService.deleteDriver(driverId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}