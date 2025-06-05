package com.p2p.transport.repository;

import com.p2p.transport.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUserId(UUID userId);
    List<Payment> findByDriverId(UUID driverId);
    Optional<Payment> findByTransportRequestId(UUID transportRequestId);
}