package com.p2p.transport.repository;

import com.p2p.transport.model.DriverAggregatedRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverAggregatedRatingRepository extends JpaRepository<DriverAggregatedRating, UUID> {
    Optional<DriverAggregatedRating> findByDriverId(UUID driverId);
}