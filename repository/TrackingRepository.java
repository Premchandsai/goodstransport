package com.p2p.transport.repository;

import com.p2p.transport.model.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TrackingRepository extends JpaRepository<Tracking, UUID> {
    @Query("SELECT t FROM Tracking t WHERE t.ride.id = :rideId ORDER BY t.updatedAt DESC LIMIT 1")
    Optional<Tracking> findLatestByRideId(UUID rideId);
}