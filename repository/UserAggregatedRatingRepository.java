package com.p2p.transport.repository;

import com.p2p.transport.model.UserAggregatedRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAggregatedRatingRepository extends JpaRepository<UserAggregatedRating, UUID> {
    Optional<UserAggregatedRating> findByUserId(UUID userId);
}
