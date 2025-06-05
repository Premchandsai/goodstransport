package com.p2p.transport.repository;

import com.p2p.transport.model.UserRatingAndReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserRatingRepository extends JpaRepository<UserRatingAndReview, UUID> {
    boolean existsByRideIdAndReviewerUserId(UUID rideId, UUID reviewerUserId);

    @Query("SELECT AVG(r.rating) FROM UserRatingAndReview r WHERE r.driver.id = :driverId")
    Double findAverageRatingByReviewedDriverId(UUID driverId);

    @Query("SELECT COUNT(r) FROM UserRatingAndReview r WHERE r.driver.id = :driverId")
    int countByReviewedDriverId(UUID driverId);

    List<UserRatingAndReview> findByReviewerUserId(UUID userId);
}