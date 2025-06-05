package com.p2p.transport.controller;

import com.p2p.transport.model.User;
import com.p2p.transport.model.Payment;
import com.p2p.transport.model.Ride;
import com.p2p.transport.model.TransportRequest;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(summary = "Get all users", description = "Admin retrieves all users.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        ApiResponse<List<User>> response = new ApiResponse<>(HttpStatus.OK.value(), "Users retrieved successfully", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all rides", description = "Admin retrieves all rides.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rides retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/rides")
    public ResponseEntity<ApiResponse<List<Ride>>> getAllRides() {
        List<Ride> rides = adminService.getAllRides();
        ApiResponse<List<Ride>> response = new ApiResponse<>(HttpStatus.OK.value(), "Rides retrieved successfully", rides);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all requests", description = "Admin retrieves all transport requests.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<TransportRequest>>> getAllRequests() {
        List<TransportRequest> requests = adminService.getAllRequests();
        ApiResponse<List<TransportRequest>> response = new ApiResponse<>(HttpStatus.OK.value(), "Requests retrieved successfully", requests);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all payments", description = "Admin retrieves all payments.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        List<Payment> payments = adminService.getAllPayments();
        ApiResponse<List<Payment>> response = new ApiResponse<>(HttpStatus.OK.value(), "Payments retrieved successfully", payments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Ban a user", description = "Admin bans a user by ID.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User banned successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable UUID userId) {
        try {
            adminService.banUser(userId);
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.OK.value(), "User banned successfully", null);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse<Void> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Generate report", description = "Admin generates a summary report.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Report generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<String>> generateReport() {
        String report = adminService.generateReport();
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Report generated successfully", report);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}