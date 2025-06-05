package com.p2p.transport.repository;

import com.p2p.transport.model.TransportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TransportRequestRepository extends JpaRepository<TransportRequest, UUID> {
    List<TransportRequest> findBySenderId(UUID senderId);
    List<TransportRequest> findByRideDriverId(UUID driverId);
}