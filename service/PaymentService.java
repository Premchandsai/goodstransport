package com.p2p.transport.service;

import com.p2p.transport.model.*;
import com.p2p.transport.model.enums.PaymentMethod;
import com.p2p.transport.model.enums.PaymentStatusEnum;
import com.p2p.transport.model.enums.RefundMethod;
import com.p2p.transport.model.enums.RefundStatus;
import com.p2p.transport.repository.*;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransportRequestRepository transportRequestRepository;
    private final RefundRepository refundRepository;
    private final BankDetailsRepository bankDetailsRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Value("${stripe.apiKey}")
    private String stripeApiKey;

    public PaymentService(PaymentRepository paymentRepository,
                          TransportRequestRepository transportRequestRepository,
                          RefundRepository refundRepository,
                          BankDetailsRepository bankDetailsRepository,
                          NotificationService notificationService,
                          UserRepository userRepository,
                          DriverRepository driverRepository) {
        this.paymentRepository = paymentRepository;
        this.transportRequestRepository = transportRequestRepository;
        this.refundRepository = refundRepository;
        this.bankDetailsRepository = bankDetailsRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public ApiResponse<Payment> processPayment(UUID requestId, BigDecimal amount, String stripeToken,
                                               PaymentMethod paymentMethod, UUID userId, UUID driverId) {
        try {
            TransportRequest request = transportRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Transport request not found"));
            if (!request.getStatus().equals("ACCEPTED")) {
                return new ApiResponse<>(400, "Request must be accepted before payment", null,
                        List.of(new ErrorDetail("INVALID_STATUS", "Request status is not ACCEPTED")));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            Charge charge = Charge.create(Map.of(
                    "amount", amount.multiply(BigDecimal.valueOf(100)).intValue(),
                    "currency", "usd",
                    "source", stripeToken,
                    "description", "Payment for transport request " + requestId
            ));

            Payment payment = Payment.builder()
                    .paymentId(UUID.randomUUID())
                    .totalAmount(amount)
                    .paymentMethod(paymentMethod)
                    .paymentStatus(PaymentStatusEnum.COMPLETED)
                    .user(user)
                    .driver(driver)
                    .transportRequest(request)
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            notificationService.sendNotification(userId, "Payment of $" + amount + " processed successfully");
            return new ApiResponse<>(200, "Payment processed successfully", savedPayment);
        } catch (StripeException e) {
            return new ApiResponse<>(400, "Payment failed: " + e.getMessage(), null,
                    List.of(new ErrorDetail("PAYMENT_FAILED", e.getMessage())));
        }
    }

    @Transactional
    public ApiResponse<Refund> processRefund(UUID paymentId, BigDecimal refundAmount, String reason, UUID userId) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            if (payment.getPaymentStatus() != PaymentStatusEnum.COMPLETED) {
                return new ApiResponse<>(400, "Only completed payments can be refunded", null,
                        List.of(new ErrorDetail("INVALID_STATUS", "Payment is not completed")));
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Refund refund = Refund.builder()
                    .id(UUID.randomUUID())
                    .user(user)
                    .payment(payment)
                    .refundAmount(refundAmount)
                    .refundStatus(RefundStatus.PROCESSED)
                    .refundMethod(RefundMethod.ORIGINAL_PAYMENT)
                    .reason(reason)
                    .build();

            Refund savedRefund = refundRepository.save(refund);
            payment.setPaymentStatus(PaymentStatusEnum.REFUNDED);
            paymentRepository.save(payment);
            notificationService.sendNotification(userId, "Refund of $" + refundAmount + " processed successfully");
            return new ApiResponse<>(200, "Refund processed successfully", savedRefund);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Refund failed: " + e.getMessage(), null,
                    List.of(new ErrorDetail("REFUND_FAILED", e.getMessage())));
        }
    }

    @Transactional
    public ApiResponse<BankDetails> addBankDetails(BankDetails bankDetails, UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            bankDetails.setUser(user);
            bankDetails.setId(UUID.randomUUID());
            BankDetails savedBankDetails = bankDetailsRepository.save(bankDetails);
            notificationService.sendNotification(userId, "Bank details added successfully");
            return new ApiResponse<>(200, "Bank details added successfully", savedBankDetails);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Failed to add bank details: " + e.getMessage(), null,
                    List.of(new ErrorDetail("BANK_DETAILS_ERROR", e.getMessage())));
        }
    }

    public ApiResponse<BankDetails> getBankDetails(UUID userId) {
        try {
            BankDetails bankDetails = bankDetailsRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Bank details not found"));
            return new ApiResponse<>(200, "Bank details retrieved successfully", bankDetails);
        } catch (Exception e) {
            return new ApiResponse<>(404, "Bank details not found: " + e.getMessage(), null,
                    List.of(new ErrorDetail("BANK_DETAILS_NOT_FOUND", e.getMessage())));
        }
    }

    public List<Payment> getSenderPaymentHistory(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getDriverPaymentHistory(UUID driverId) {
        return paymentRepository.findByDriverId(driverId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}