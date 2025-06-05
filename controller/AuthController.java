package com.p2p.transport.controller;

import com.p2p.transport.model.User;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        ApiResponse<User> response = authService.register(user);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "User login")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        ApiResponse<String> response = authService.login(loginRequest.email(), loginRequest.password());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Reset password")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        ApiResponse<String> response = authService.resetPassword(request.email(), request.newPassword());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public record LoginRequest(String email, String password) {}
    public record ResetPasswordRequest(String email, String newPassword) {}
}