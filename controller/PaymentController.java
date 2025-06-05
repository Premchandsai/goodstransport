package com.p2p.transport.controller;

import com.p2p.transport.model.BankDetails;
import com.p2p.transport.model.Payment;
import com.p2p.transport.model.Refund;
import com.p2p.transport.model.enums.PaymentMethod;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import com.p2p.transport.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Process payment", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment processed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Payment failed")
    })
    @PostMapping("/{requestId}")
    public ResponseEntity<ApiResponse<Payment>> processPayment(
            @PathVariable UUID requestId,
            @RequestParam BigDecimal amount,
            @RequestParam String stripeToken,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam UUID userId,
            @RequestParam UUID driverId) {
        try {
            ApiResponse<Payment> response = paymentService.processPayment(requestId, amount, stripeToken, paymentMethod, userId, driverId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            ApiResponse<Payment> response = new ApiResponse<>(400, "Payment failed", null);
            response.setErrors(List.of(new ErrorDetail("PAYMENT_FAILED", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Process refund", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Refund processed"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Refund failed")
    })
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<Refund>> processRefund(
            @PathVariable UUID paymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam String reason,
            @RequestParam UUID userId) {
        try {
            ApiResponse<Refund> response = paymentService.processRefund(paymentId, refundAmount, reason, userId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            ApiResponse<Refund> response = new ApiResponse<>(400, "Refund failed", null);
            response.setErrors(List.of(new ErrorDetail("REFUND_FAILED", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Add bank details", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bank details added"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid bank details")
    })
    @PostMapping("/bank-details")
    public ResponseEntity<ApiResponse<BankDetails>> addBankDetails(
            @RequestBody BankDetails bankDetails,
            @RequestParam UUID userId) {
        try {
            ApiResponse<BankDetails> response = paymentService.addBankDetails(bankDetails, userId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            ApiResponse<BankDetails> response = new ApiResponse<>(400, "Invalid bank details", null);
            response.setErrors(List.of(new ErrorDetail("INVALID_BANK_DETAILS", e.getMessage())));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Get bank details", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bank details retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bank details not found")
    })
    @GetMapping("/bank-details")
    public ResponseEntity<ApiResponse<BankDetails>> getBankDetails(@RequestParam UUID userId) {
        try {
            ApiResponse<BankDetails> response = paymentService.getBankDetails(userId);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            ApiResponse<BankDetails> response = new ApiResponse<>(404, "Bank details not found", null);
            response.setErrors(List.of(new ErrorDetail("NOT_FOUND", e.getMessage())));
            return ResponseEntity.status(404).body(response);
        }
    }

    @Operation(summary = "Get sender payment history", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment history retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/sender/{userId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getSenderPaymentHistory(@PathVariable UUID userId) {
        List<Payment> payments = paymentService.getSenderPaymentHistory(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Sender payment history retrieved successfully", payments));
    }

    @Operation(summary = "Get driver payment history", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment history retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getDriverPaymentHistory(@PathVariable UUID driverId) {
        List<Payment> payments = paymentService.getDriverPaymentHistory(driverId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Driver payment history retrieved successfully", payments));
    }

    @Operation(summary = "Get all payments (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payments retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(new ApiResponse<>(200, "Payments retrieved successfully", payments));
    }
}