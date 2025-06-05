package com.p2p.transport.repository;

import com.p2p.transport.model.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DriverRatingRepository extends JpaRepository<DriverRating, UUID> {
    boolean existsByRideIdAndReviewerDriverId(UUID rideId, UUID driverId);

    @Query("SELECT AVG(r.rating) FROM DriverRating r WHERE r.reviewedUser.id = :userId")
    Double findAverageRatingByReviewedUserId(UUID userId);

    @Query("SELECT COUNT(r) FROM DriverRating r WHERE r.reviewedUser.id = :userId")
    int countByReviewedUserId(UUID userId);

    List<DriverRating> findByReviewerDriverId(UUID driverId);
}